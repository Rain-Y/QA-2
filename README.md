# QA
QA小分队之家

*** zhwiki-20141009-pages-articles-multistream.xml.bz2
项目要求使用中文维基百科所有完整内容文件 (2014-10-09 版本)，百度网盘链接为
http://pan.baidu.com/share/home?uk=3020389064#category/type=0

*** Wiki_extractor.py
用于解析wiki的dump文件的工具，详见http://medialab.di.unipi.it/wiki/Wikipedia_Extractor
运行环境：Python 2（下载链接https://www.python.org/downloads/release/python-2710/）
注意：如果Windows下有问题（我就遇到了），可以在Ubuntu下运行。建议就在Ubuntu下解析，Mac不清楚~ Ubuntu自带Python，不用下载

命令格式是
WikiExtractor.py [options] xml-dump-file

optional arguments:
 -h, --help            show this help message and exit
 -o OUTPUT, --output OUTPUT
                       output directory
 -b n[KMG], --bytes n[KMG]
                       put specified bytes per output file (default is 1M)
 -B BASE, --base BASE  base URL for the Wikipedia pages
 -c, --compress        compress output files using bzip
 -l, --links           preserve links
 -ns ns1,ns2, --namespaces ns1,ns2
                       accepted namespaces
 -q, --quiet           suppress reporting progress info
 -s, --sections        preserve sections
 -a, --article         analyze a file containing a single article
 --templates TEMPLATES
                       use or create file containing templates
 -v, --version         print program version
 
 例：进入工作目录后，执行
 python Wiki_extractor.py -o wiki -b 1G zhwiki-20141009-pages-articles-multistream.xml.bz2
 那么会将xml.bz2解析到wiki文件夹中，文件大小不超过1G（足够放下整个解析后的文件）
 