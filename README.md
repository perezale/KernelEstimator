# Gaussian Kernel Estimator

Simple kernel density estimator. Uses one gaussian kernel per observed data value.

This implementation builds a probability density function (pdf), which represents the probability density of the received signal strength on Bluetooth Radio Signal Strength over the packet distance.
Packets are grouped by Signal Ranges. Standard output reveals the evaluation of the resulting density function for each series.

### Dependencies
 - [Weka] - Collection of machine learning algorithms for data mining tasks
 - [Jackson-Dataformat-CSV] - Jackson data format module for reading and writing CSV encoded data

### Version
1.0

### Running

You need Maven and a sample csv file:

```sh
$ git clone [git-repo-url]
$ cd dillinger
```


License
----

MIT

   [Weka]: <http://weka.sourceforge.net/>
   [Jackson-Dataformat-CSV]: <https://github.com/FasterXML/jackson-dataformat-csv>
  

