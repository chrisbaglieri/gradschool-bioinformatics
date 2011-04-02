#!/bin/csh

##############################################
# Returns the alignment length and RMSD      #
# of sequences found both in CE and BLAST    #
# author: chris baglieri                     #
##############################################

# start MySQL
mysql  << EOF

# specify the homework 10 database
use cbaglieri_hw10;

SELECT DISTINCT
        Chain.pdb_chain             AS 'PDB_CODE',
        FSSP_ali.lali               AS 'FSSP_ALN_LENGTH',
        FSSP_ali.rmsd               AS 'RMSD'
FROM    Chain
INNER JOIN FSSP_ali
    ON  FSSP_ali.chain_id = Chain.chain_id
INNER JOIN CE_ali
    ON  CE_ali.chain_id = FSSP_ali.chain_id;

# quit MySQL
quit

EOF