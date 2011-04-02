#!/bin/csh

##############################################
# Returns the results of FSSP alignments     #
# sorted by the alignment length             #
# author: chris baglieri                     #
##############################################

# start MySQL
mysql  << EOF

# specify the homework 10 database
use cbaglieri_hw10;

# select the FSSP alignments sorted by length
SELECT 
        Chain.pdb_chain AS 'PDB_ID',
        Z               AS 'Z_Score',
        ide             AS 'Identity',
        rmsd            AS 'RMSD',
        lali            AS 'Length'
FROM    FSSP_ali
INNER JOIN Chain
    ON  Chain.chain_id = FSSP_ali.chain_id
ORDER BY FSSP_ali.lali;

# quit MySQL
quit

EOF