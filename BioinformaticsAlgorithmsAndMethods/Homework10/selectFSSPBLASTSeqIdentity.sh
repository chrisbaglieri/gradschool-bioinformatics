#!/bin/csh

##############################################
# Returns the sequence identities from       #
# BLAST and FSSP query results.              #
# author: chris baglieri                     #
##############################################

# start MySQL
mysql  << EOF

# specify the homework 10 database
use cbaglieri_hw10;

SELECT 
        Chain.pdb_chain     AS 'PDB_CHAIN',
        BLAST_ali.identity  AS 'BLAST_SEQ_IDENTITY',
        FSSP_ali.ide        AS 'FSSP_SEQ_IDENTITY'
FROM    Chain
INNER JOIN FSSP_ali
    ON  FSSP_ali.chain_id = Chain.chain_id
INNER JOIN BLAST_ali
    ON  BLAST_ali.chain_id = Chain.chain_id;

# quit MySQL
quit

EOF