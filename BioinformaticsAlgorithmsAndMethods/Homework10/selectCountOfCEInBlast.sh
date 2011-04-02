#!/bin/csh

##############################################
# Returns ths number of sequences found by   #
# CE and by BLAST                            #
# author: chris baglieri                     #
##############################################

# start MySQL
mysql  << EOF

# specify the homework 10 database
use cbaglieri_hw10;

SELECT DISTINCT
        COUNT(1)
FROM    Chain
INNER JOIN BLAST_ali
    ON  BLAST_ali.chain_id = Chain.chain_id
INNER JOIN CE_ali
    ON  CE_ali.chain_id = BLAST_ali.chain_id;

# quit MySQL
quit

EOF