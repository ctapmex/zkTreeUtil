==========================================
zkTreeUtil - ZooKeeper Tree Utility
Author: Dobrunov Aleksey (ctapmex)
Homepage: https://github.com/ctapmex/zkTreeUtil
==========================================

zkTreeUtil это приложение для работы с данными/деревом, хранимыми в ZooKeeper.
Текущая основная задача - экспорт содержимого ZooKeeper.

usage: zkTreeUtil
 -e,--export                       exports the zookeeper tree
 -od,--output-dir <dir>            output directory to which znode
                                   information should be written (must be
                                   a normal, empty directory)
 -of,--output-file <filename>      output file to which znode information
                                   should be written
 -ox,--output-xmlfile <filename>   output xml-file to which znode
                                   information should be written
 -p,--path <znodepath>             path to the zookeeper subtree rootnode.
 -z,--zookeeper <zkhosts>          zookeeper remote servers (ie
                                   "localhost:2181")

Экспорт производится в трех форматах:
 - файловая структура (output-dir) 
    В заданной для вывода директории, для каждого узла создается либо папка, 
    либо файл. Повторяя древовидную структуру. Если для узла задано значение
    (value), то оно сохраняется в файл. Для узлов-папок значение сохраняется в
    файл '_znode'.
    Главное ограничение данного формата - файловая ситема поддерживает не все 
    символы в имени. Например ':'.
 - плоский файл (output-file)
    В заданном для вывода файле, создаются строки вида
    path=/ss	val=dd	type='ephemeral'
    path - путь узла.
    val - значение, если задано
    type - тип узла. ephemeral - эфемерный/временный узел.
 - xml файл  
    В заданном для вывода файле, создаются узлы (znode), хранящие всю
    информацию в атрибутах

Для возможности экспорта определенного поддерева ZooKeeper, предназначен ключ 'p'.
Параметр 'z', адрес сервера, поддерживает указание нескольких серверов
кластера ZooKeeper через запятую. Например, 127.0.0.1:3000,127.0.0.1:3001

Примеры использования:
zktreeutil -z 127.0.0.1:2181 --export -xf d:\test.xml -p /ss
zktreeutil -z 127.0.0.1:2181 --export -od d:\test

Инструкция по сборке
------------------
1. войти в директорию с исходниками
2. mvn clean package
3. дистрибутив 'zkTreeUtil' будет создан в директории target\distrib

