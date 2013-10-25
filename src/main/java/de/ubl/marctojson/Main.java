package de.ubl.marctojson;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mapdb.DB;
import org.mapdb.DBMaker;

public class Main {

	private static final String CACHE_DIR = Utils.combine(
			System.getProperty("user.home"), ".marctojson", "cache.mapdb");

	private static Logger logger = Logger.getLogger(Main.class
			.getCanonicalName());

	@SuppressWarnings("static-access")
	public static void main(String... args) throws Exception {

		// cache for file metadata, e.g. MARC record count or number of lines
		// for JSON files -- speeds up the integrity tests between MARC and JSON
		// files
		try {
			Utils.createDirectories(new File(CACHE_DIR).getParent());
		} catch (IOException e) {
			logger.error("could not create cache directory: " + CACHE_DIR);
			System.exit(1);
		}
		final DB db = DBMaker.newFileDB(new File(CACHE_DIR))
				.compressionEnable().closeOnJvmShutdown().make();
		if (db.getHashMap("default") == null) {
			db.createHashMap("default", true, null, null);
		}

		System.setProperty("org.marc4j.marc.MarcFactory",
				"de.ubl.marctojson.FatMarcFactory");

		Options options = new Options();

		options.addOption(OptionBuilder.hasArg()
				.withDescription("path to MARC file").withLongOpt("input")
				.withArgName("FILE").create("i"));

		options.addOption(OptionBuilder.hasArg()
				.withDescription("path to output file (console if none given)")
				.withLongOpt("output").withArgName("FILE").create("o"));

		options.addOption(OptionBuilder.hasArg()
				.withDescription("input encoding (UTF-8)")
				.withLongOpt("input-encoding").withArgName("NAME").create("f"));

		options.addOption(OptionBuilder.hasArg()
				.withDescription("output encoding (UTF-8)")
				.withLongOpt("output-encoding").withArgName("NAME").create("t"));

		options.addOption(OptionBuilder
				.hasArg()
				.withDescription(
						"key=value pair(s) to inject into meta field (repeatable)")
				.withLongOpt("metadata").withArgName("STRING").create("m"));

		options.addOption(OptionBuilder.withDescription("show help")
				.withLongOpt("help").create("h"));

		options.addOption(OptionBuilder
				.withDescription("show processing speed")
				.withLongOpt("verbose").create("v"));

		options.addOption(OptionBuilder
				.hasArg()
				.withArgName("FILE")
				.withDescription(
						"where to log messages (if not specified, log to stderr only)")
				.withLongOpt("logfile").create("l"));

		options.addOption(OptionBuilder.withDescription("use DEBUG log level")
				.withLongOpt("debug").create("g"));

		options.addOption(OptionBuilder.withDescription("show version")
				.withLongOpt("version").create());

		options.addOption(OptionBuilder
				.withDescription("show available encodings")
				.withLongOpt("list-encodings").create("e"));

		final CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException ex) {
			System.err.println(ex.getLocalizedMessage());
			System.exit(1);
		}

		// misc
		final Properties appProperties = new Properties();
		appProperties.load(appProperties.getClass().getResourceAsStream(
				"/application.properties"));

		if (cmd.hasOption("version")) {
			System.out.println(appProperties.get("app.executable") + " "
					+ appProperties.get("app.version") + " "
					+ appProperties.get("app.version.date") + " ["
					+ appProperties.get("app.homepage") + "]");
		}

		if (cmd.hasOption("h")) {
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("marctojson",
					"Converts MARC to elasticsearch-flavored JSON.", options,
					"Learn more at " + appProperties.get("app.homepage"), true);
			System.exit(0);
		}

		if (cmd.hasOption("list-encodings")) {
			Utils.dumpAvailableEncodings();
			System.exit(0);
		}

		// logging
		final Properties props = new Properties();
		props.load(props.getClass().getResourceAsStream("/log4j.properties"));

		if (cmd.hasOption("debug")) {
			props.setProperty("log4j.logger.de.ubl", "DEBUG");
			logger.setLevel(Level.DEBUG);
		}

		if (cmd.hasOption("logfile")) {
			props.setProperty("log4j.rootLogger", "WARN, consoleAppender, F");
			props.setProperty("log4j.appender.F",
					"org.apache.log4j.FileAppender");
			props.setProperty("log4j.appender.F.File",
					cmd.getOptionValue("logfile"));
			props.setProperty("log4j.appender.F.layout",
					"org.apache.log4j.PatternLayout");
			props.setProperty("log4j.appender.F.layout.ConversionPattern",
					"[%d{ISO8601}][%-5p][%-25c] %m%n");
		}

		PropertyConfigurator.configure(props);

		// metadata
		Map<String, Object> metadata = new HashMap<String, Object>();
		if (cmd.hasOption("metadata")) {
			try {
				metadata = Utils.getMapForKeys(cmd.getOptionValues("metadata"));
				logger.debug(metadata);
			} catch (ParseException ex) {
				logger.error(ex.getMessage());
				System.exit(1);
			}
		}

		// I/O
		if (cmd.hasOption("o") && cmd.hasOption("i")) {
			final MarcFile marcFile = new MarcFile(cmd.getOptionValue("i"));
			final FileWithCountableLines jsonFile = new FileWithCountableLines(
					cmd.getOptionValue("o"));

			marcFile.setCache(db);
			jsonFile.setCache(db);

			if (jsonFile.exists()) {
				if (Utils.inSync(marcFile, jsonFile)) {
					logger.debug("up-to-date");
					System.exit(0);
				} else {
					logger.fatal("previously generated "
							+ jsonFile.getAbsolutePath()
							+ " seems inconsistent with input in "
							+ marcFile.getAbsolutePath());
					logger.fatal("remove " + jsonFile.getAbsolutePath()
							+ " manually and try again.");
					System.exit(1);
				}
			}
		}

		String outputFilename = cmd.getOptionValue("o");
		String outputEncoding = cmd.getOptionValue("t",
				appProperties.getProperty("app.output.encoding"));
		String inputEncoding = cmd.getOptionValue("f",
				appProperties.getProperty("app.input.encoding"));

		// convert
		Converter converter = new Converter();
		long counter = converter.convert(cmd.getOptionValue("input"),
				inputEncoding, outputFilename, outputEncoding, metadata);

		logger.info("converted " + counter + " records");

	}
}
