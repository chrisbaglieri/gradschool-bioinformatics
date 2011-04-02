#!/bin/csh

# start MySQL
mysql  << EOF

# specify the homework 10 database
use cbaglieri_hw10;

# generate the Chain table
drop table if exists Chain;
create table Chain
(
    chain_id  int        NOT NULL PRIMARY KEY AUTO_INCREMENT,
    pdb_chain varchar(5) BINARY NOT NULL,
    chainLen  smallint   UNSIGNED,
    seqPDB    text,
    INDEX chain_idx(chain_id),
    UNIQUE INDEX chian_pdb_idx(pdb_chain)
) AUTO_INCREMENT = 1;

# generte the BLAST alignment table
drop table if exists BLAST_ali;
create table BLAST_ali
(
    ali_id           int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    chain_id         int NOT NULL,
    date             datetime,
    identity         float,
    alignment_length smallint UNSIGNED,
    mismatches       smallint UNSIGNED,
    gap_openings     smallint UNSIGNED,
    q_start          smallint,
    q_end            smallint,
    s_start          smallint,
    s_end            smallint,
    e_value          float,
    bit_score        float,
    FOREIGN KEY (chain_id) references Chain(chain_id) ON DELETE CASCADE,
    INDEX chain_idx(chain_id),
) AUTO_INCREMENT = 1;

# generate the FSSP alignment table
drop table if exists FSSP_ali;
create table FSSP_ali
(
    ali_id           int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    chain_id         int NOT NULL,
    date             datetime,
    Z                float,
    ide              float,
    rmsd             float,
    lali             smallint UNSIGNED,
    lseq2            smallint UNSIGNED,
    FOREIGN KEY (chain_id) references Chain(chain_id) ON DELETE CASCADE,
    INDEX chain_idx(chain_id),
) AUTO_INCREMENT = 1;

# generate the CE alignment table
drop table if exists CE_ali;
create table CE_ali
(
    ali_id           int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    chain_id         int NOT NULL,
    date             datetime,
    Z_Score          float,
    RMSD             float,
    Seq              float,
    Aligned          smallint UNSIGNED,
    Size             smallint UNSIGNED,
    Gap              smallint UNSIGNED,
    FOREIGN KEY (chain_id) references Chain(chain_id) ON DELETE CASCADE,
    INDEX chain_idx(chain_id),
) AUTO_INCREMENT = 1;

##############################
# new tables for homework 10 #
##############################


# sequence table
drop table if exists Sequence;
create table Sequence
(
    sequence_id  int        NOT NULL PRIMARY KEY AUTO_INCREMENT,
    sequence     text       NOT NULL,
    INDEX sequence_idx(sequence_id)
) AUTO_INCREMENT = 1;

# sequence alignment map table
drop table if exists Sequence_Alignment_Map;
create table Sequence_Alignment_Map
(
    sequence_id      int NOT NULL,
    alignment_id     int NOT NULL,
    startPosition    int NOT NULL,
    endPosition      int NOT NULL,
    FOREIGN KEY (sequence_id) references Sequence(sequence_id)
);


#############################
##### end new tables ########
#############################

# show the tables just created
show tables;

# quit MySQL
quit

EOF