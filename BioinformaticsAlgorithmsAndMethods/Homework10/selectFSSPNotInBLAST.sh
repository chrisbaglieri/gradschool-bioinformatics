#!/bin/csh

##############################################
# Returns the results of FSSP alignments     #
# not found in the BLAST results             #
# author: chris baglieri                     #
##############################################

# start MySQL
mysql  << EOF

# specify the homework 10 database
use cbaglieri_hw10;

SELECT DISTINCT 
        Chain.pdb_chain AS 'PDB_CHAIN'
FROM    Chain
INNER JOIN BLAST_ali
    ON  BLAST_ali.chain_id = Chain.chain_id
INNER JOIN FSSP_ali
    ON  FSSP_ali.chain_id != BLAST_ali.chain_id;

# quit MySQL
quit

EOF