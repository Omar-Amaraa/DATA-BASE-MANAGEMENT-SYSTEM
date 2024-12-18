# PROJET_BDDA_alpha
Ce projet a pour objectif la mise en place d’une base de données simple (un « mini SGBD ») dans le cadre de notre UE de Base de données avancées. Il inclut des fonctionnalités telles que :
Gestion des pages disque
Gestion des buffers
Structures de données internes
Création et gestion de bases de données et de tables
Insertion, sélection et manipulation de données
Fichiers de configuration et scripts d’exécution

## Arborescence du Projet
PROJET_BDDA_alpha/
├── lib/
├── src/
│   ├── main/java/org/example/
│   │   ├── BPlusTree.java
│   │   ├── BPlusTreeNode.java
│   │   ├── Buffer.java
│   │   ├── BufferManager.java
│   │   ├── ColInfo.java
│   │   ├── ColType.java
│   │   ├── Condition.java
│   │   ├── DBConfig.java
│   │   ├── DBIndexManager.java
│   │   ├── DBManager.java
│   │   ├── DataPageHoldRecordIterator.java
│   │   ├── Database.java
│   │   ├── DiskManager.java
│   │   ├── IRecordIterator.java
│   │   ├── PageDirectoryIterator.java
│   │   ├── PageId.java
│   │   ├── PageOrientedJoinOperator.java
│   │   ├── ProjectOperator.java
│   │   ├── Record.java
│   │   ├── RecordId.java
│   │   ├── RecordPrinter.java
│   │   ├── Relation.java
│   │   ├── RelationScanner.java
│   │   ├── SGBD.java
│   │   └── SelectOperator.java
│   └── test/java/
│       ├── BPlusTreeTest.java
│       ├── BufferManagerTest.java
│       ├── DBConfigTest.java
│       ├── DatabaseTest.java
│       ├── DBManagerTest.java
│       ├── DiskManagerTests.java
│       ├── RelationTest.java
│       └── SGBDTest.java
├── .gitignore
├── MiniSGBD.bat
└── MiniSGBD.sh

## Pré-requis
Java 8 ou supérieur
Un système d’exploitation supportant les scripts fournis (.bat sous Windows, .sh sous Linux/Unix)


