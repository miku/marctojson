marctojson
==========

Converts MARC to JSON.

    $ marctojson -h
    usage: marctojson [-e] [-f <NAME>] [-g] [-h] [-i <FILE>] [-l <FILE>] [-m
           <STRING>] [-o <FILE>] [-t <NAME>] [-v] [--version]
    Converts MARC to elasticsearch-flavored JSON.
     -e,--list-encodings           show available encodings
     -f,--input-encoding <NAME>    input encoding (UTF-8)
     -g,--debug                    use DEBUG log level
     -h,--help                     show help
     -i,--input <FILE>             path to MARC file
     -l,--logfile <FILE>           where to log messages (if not specified,
                                   log to stderr only)
     -m,--metadata <STRING>        key=value pair(s) to inject into meta field
                                   (repeatable)
     -o,--output <FILE>            path to output file (console if none given)
     -t,--output-encoding <NAME>   output encoding (UTF-8)
     -v,--verbose                  show processing speed
        --version                  show version
    Learn more at https://github.com/miku/marctojson