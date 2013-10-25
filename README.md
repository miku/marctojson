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


Building
--------

    $ git clone git@github.com:miku/marctojson.git
    $ cd marctojson
    $ mvn package

Packaging will create a single standalone executable under `target/marctojson`.


Usage example
-------------


One line - containing a JSON object - is emitted per MARC record:

    $ target/marctojson -i src/test/resources/423_records.mrc | wc -l
    423


Pass meta information to the document with `--metadata`:

    $ target/marctojson -m hello=world -m date=`date +"%Y-%m-%d"` -i src/test/resources/vanilla.mrc
    {"content":{"300":[{"c":"30x21  ...
    ... "meta":{"hello":"world","date":"2013-10-25"}}


Some fields are abridged for readablity. Using [json_pp](http://search.cpan.org/~makamaka/JSON-PP-2.27103/bin/json_pp).

    $ target/marctojson -i src/test/resources/vanilla.mrc|json_pp
    {
       "content_type" : "application/marc",
       "sha1" : "9d003e3ae301bb035082d6a548803cd282fde46e",
       "original" : "01013nam a22002652a ....",
       "content" : {
          "365" : [
             {
                "a" : "02",
                "m" : "Construction Research Communications Ltd",
                "d" : "00",
                "2" : "onix-pt",
                "j" : "GB",
                "c" : "GBP",
                "h" : "Z 16.00 0.0 16.00 0.00",
                "k" : "xxk",
                "ind1" : " ",
                "b" : "16.00",
                "ind2" : " "
             }
          ],
          "leader" : {
             "impldef2" : "2a ",
             "subfieldcodelength" : 2,
             "entrymap" : "4500",
             "status" : "n",
             "codingschema" : "a",
             "length" : 1013,
             "indicatorcount" : 2,
             "impldef1" : "m ",
             "type" : "a",
             "raw" : "01013nam a22002652a 4500"
          },
          "245" : [
             {
                "c" : "A. Dunster, K. Quillin.",
                "ind1" : "1",
                "a" : "Applications, performance characteristics ...",
                "ind2" : "0"
             }
          ],
          "700" : [
             {
                "ind1" : "1",
                "a" : "Quillin, K.",
                "ind2" : " "
             }
          ],
          "005" : "20130604000000.0",
          "008" : "130525e201306uuxxk    | |||||||0|0 eng|d",
          "003" : "UK-WkNB",
          "007" : "ta",
          "072" : [
             {
                "ind1" : " ",
                "a" : "TNK",
                "ind2" : "7",
                "2" : "bicssc"
             },
             {
                "ind1" : " ",
                "a" : "HOU",
                "ind2" : "7",
                "2" : "eflch"
             }
          ],
          "366" : [
             {
                "m" : "Construction Research Communications Ltd",
                "d" : "20130630",
                "2" : "UK-WkNB",
                "j" : "GB",
                "c" : "NP 20130525",
                "k" : "xxk",
                "ind1" : " ",
                "b" : "20130605",
                "ind2" : " "
             }
          ],
          "040" : [
             {
                "c" : "UK-WkNB",
                "ind1" : " ",
                "a" : "UK-WkNB",
                "b" : "eng",
                "ind2" : " "
             }
          ],
          "500" : [
             {
                "ind1" : " ",
                "a" : "Pamphlet.",
                "ind2" : " "
             }
          ],
          "100" : [
             {
                "ind1" : "1",
                "a" : "Dunster, A.",
                "ind2" : " "
             }
          ],
          "300" : [
             {
                "c" : "30x21 cm.",
                "ind1" : " ",
                "a" : "8 p. ;",
                "ind2" : " "
             }
          ],
          "650" : [
             {
                "ind1" : " ",
                "a" : "Building construction & materials.",
                "ind2" : "7",
                "2" : "bicssc"
             },
             {
                "ind1" : " ",
                "a" : "House and Home.",
                "ind2" : "7",
                "2" : "eflch"
             }
          ],
          "001" : "9781848063334",
          "260" : [
             {
                "c" : "2013.",
                "ind1" : " ",
                "a" : "Bracknell :",
                "b" : "IHS BRE Press :",
                "ind2" : " "
             },
             {
                "ind1" : " ",
                "b" : "[distributor] IHS BRE Press,",
                "ind2" : " "
             }
          ],
          "020" : [
             {
                "c" : "£16.00",
                "ind1" : " ",
                "a" : "9781848063334 :",
                "ind2" : " "
             },
             {
                "c" : "£16.00",
                "ind1" : " ",
                "a" : "1848063334 :",
                "ind2" : " "
             }
          ]
       },
       "meta" : {}
    }


Conversion speed
----------------

Converting 5457095 records (1.3Gb) takes about 30 minutes,
so around 3000 records per second.
