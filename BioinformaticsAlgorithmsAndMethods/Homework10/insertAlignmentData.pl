#!/usr/bin/perl

###############################################
# Script that parses different file format    #
# inserting the contents into into the        #
# homework10 database. Current supported      #
# data formats: BLAST, FSSP, and CE           #
#                                             #
# original author: alex abyzov                # 
# updates by: chris baglieri                  #
# bioinformatics algorithms & methods         #
###############################################

use strict;
use warnings;
use DBI;

# assign the usage for the fill script
my $usageString = "Usage: insertAlignmentData.pl [-ce,-fssp,-blast] file";

# database
my $databaseName = "cbaglieri_hw10";

# supported data formats
my $CE_DB = 1;
my $FSSP_DB = 2;
my $BLAST_DB = 3;

# grab the set of command line arguments
my $dataType = $ARGV[0];

# validate the arguments were provided
unless ($dataType) {
    print $usageString,"\n";
    exit;
}

# determine the data format, if unsupported
# type was provided, display usage and exit
my $dataFormat = 0;
if ($dataType eq "-ce") {
    $dataFormat = $CE_DB;
} elsif ($dataType eq "-fssp") {
    $dataFormat = $FSSP_DB;
} elsif ($dataType eq "-blast") {
    $dataFormat = $BLAST_DB;
} else {
    print "Not a supported data type: ",$dataType,"\n";
    print $usageString,"\n";
    exit;
}

# validate that a source file was provided
my $fileName = $ARGV[1];
unless ($fileName) {
    print "No file name given.","\n";
    print $usageString,"\n";
    exit;
}

# grab the user name from the set of environment variables
my $userName = $ENV{'USER'};

# open the database
my $dbHandle = open_database( $databaseName, $userName );

# call the appropriate data loading method
if ($dataFormat == $CE_DB) {
    &fill_ce_data( $fileName, $dbHandle );
} elsif ($dataFormat == $FSSP_DB) {
    &fill_fssp_data( $fileName, $dbHandle );
} elsif ($dataFormat == $BLAST_DB) {
    &fill_blast_data( $fileName, $dbHandle );
} else {
    # this will never be reached since we are
    # defensive when we assign the data format
    # earlier in the script
    exit;
}

# exit the main program
exit;


############## open_database ################
# Opens a handle to the specified database  #
# parameters: $databaseName, $userName      #
# return: database handle                   #
#############################################
sub open_database {
    
    # grab the arguments
    my ($db,$user) = @_;
    
    # connect to the database.  note the MySQL connection
    # string.  if an error is encountered in connecting to
    # the database, throw away the handle and return null
    my $databaseHandle = DBI->connect("DBI:mysql:$db:localhost:3306",$user,"") or
	die DBI->errstr;
    
    return $databaseHandle;
    
}

############## fill_blast_data ##################
# Inserts the BLAST data into the BLAST tables  #
# parameters: $sourceFile, $databaseHandle      #
#################################################
sub fill_blast_data {

    # grab the arguments
    my ($file_name,$dbh) = @_;

    # get the content of the BLAST file
    my @lines = &get_file_content($file_name);

    # iterate over the lines in the source file
    foreach my $line(@lines) {

        # skip over the header lines
    	if ($line =~ /Header/) { next; }
	    if ($line =~ /^\#/)    { next; }

	    my @tokens = split(/\s+/,$line);
	    my $len = @tokens;
	    if ($len == 0) { next; }

        # construct INSERT statement
    	my $query = "INSERT into BLAST_ali (";
	    $query .= "chain_id,";
	    $query .= "identity,";
	    $query .= "alignment_length,";
	    $query .= "mismatches,";
	    $query .= "gap_openings,";
	    $query .= "q_start,";
	    $query .= "q_end,";
	    $query .= "s_start,";
	    $query .= "s_end,";
	    $query .= "e_value,";
	    $query .= "bit_score";
	    $query .= ") VALUES (";
           
        # iterate over each sequence, construct
        # the set of values, and insert the
        # chain data.  relate the BLAST entry 
        # to the chain table entry
	    for (my $i = 0;$i < $len;$i++) {
	        if ($i == 0) {
		        next;
	        } elsif ($i == 1) { 
		        my $pdb_chain = &get_pdb($tokens[$i]);
        		unless ($pdb_chain) { next; }
		        my $chain_id = &insert_chain_data($pdb_chain,$dbh);
        		unless ($chain_id) { next; }
		        $query .= $chain_id;
    	    } else {
	        	$query .= ",".$tokens[$i];
	        }
	    }
	
        # close the query
        $query .= ");";
        
        # print the query for informational purposes
	    print $query,"\n";
        
        # execute the query against the database
	    $dbh->do($query);
        
    }
    
}


############## fill_fssp_data ###################
# Inserts the FSSP data into the FSSP tables    #
# parameters: $sourceFile, $databaseHandle      #
#################################################
sub fill_fssp_data {

    # grab the arguments
    my ($file_name,$dbh) = @_;

    # get the FSSP source file content
    my @lines = &get_file_content($file_name);

    # iterate over each line in the source file
    foreach my $line(@lines) {

    	my @tokens = split(/\s+/,$line);
	    my $len = @tokens;
    	if ($len == 0) { next; }

        # construct the INSERT statement
    	my $query = "INSERT into FSSP_ali (";
    	$query .= "chain_id,";
	    $query .= "Z,";
    	$query .= "ide,";
	    $query .= "rmsd,";
    	$query .= "lali,";
	    $query .= "lseq2";
    	$query .= ") VALUES (";
        
        # iterate over each sequence, construct
        # the set of values, and insert the
        # chain data.  relate the FSSP entry 
        # to the chain table entry
	    for (my $i = 0;$i < $len;$i++) {
    	    if ($i == 0 || $i == 1) {
	    	    next;
	        } elsif ($i == 2) { 
		        my $pdb_chain = $tokens[$i];
        		unless ($pdb_chain) { next; }
		        if (length($pdb_chain) == 4) {
        		    $pdb_chain .= "_";
		        }
        		my $chain_id = &insert_chain_data($pdb_chain,$dbh);
		        unless ($chain_id) { next; }
        		$query .= $chain_id;
	        } elsif ($i > 7) {
        		last;
	        } else {
		        $query .= ",".$tokens[$i];
    	    }
	    }

        # close the query        
	    $query .= ");";
        
        # print the query for informational purposes
	    print $query,"\n";
        
        # execute the query
	    $dbh->do($query);
    
    }
    
}


############## fill_ce_data #####################
# Inserts the CE data into the CE tables        #
# parameters: $sourceFile, $databaseHandle      #
#################################################
sub fill_ce_data {
    
    # grab the arguments
    my ($file_name,$dbh) = @_;

    # get the CE source file content
    my @lines = &get_file_content($file_name);
    
    # iterate over each line in the source file
    my $lineIndex = 0;
    my $numberOfLines = @lines;
    while ( $lineIndex < $numberOfLines ) {

    	my @tokens = split(/\t/,$lines[$lineIndex]);
	    my $tokenCount = @tokens;

        # construct the INSERT statement
    	my $query = "INSERT into CE_ali (";
    	$query .= "chain_id,";
    	$query .= "Z_score,";
	    $query .= "RMSD,";
    	$query .= "Seq,";
	    $query .= "Aligned,";
        $query .= "Size,";
        $query .= "Gap";
    	$query .= ") VALUES (";
        
        # iterate over each sequence and construct the set 
        # of values. note that this loop accounts for the
        # 'neighbor' and '#/#' items in the CE results
	    for (my $index = 0; $index < $tokenCount; $index++) {
            
            # insert the chain data and retrieve the ID
            if ( $index == 0 ) { 
                my ($pdbCode ) = $tokens[$index];
                $pdbCode =~ s/://;
                my ($chainId) = &insert_chain_data($pdbCode,$dbh);
                $query .= $chainId;
            }
            
            # parse the 'alignment / length' item from the results
            elsif ( $index == 4 ) {  
                my (@alignmentItems) = split(/\//, $tokens[$index]);
                $query .= "," . $alignmentItems[0];
                $query .= "," . $alignmentItems[1];
                
            # remaining tokens are not needed for the query
            }  elsif ( $index > 5 ) {
        		last;
            
            # normal entry, append to the query
	        } else {
    	        $query .= "," . $tokens[$index];
            }
	    }

        # close the query        
	    $query .= ");";
        
        # print the query for informational purposes
	    print $query,"\n";
        
        # execute the query
	    $dbh->do($query);
        
        # move to the next sequence
        ++$lineIndex;
    
    }
    
}

################# get_pdb #####################
# Gets the PDB chain from the chain argument  #
# parameters: $line (e.g. chain data)         #
# return: PDB information                     #
###############################################
sub get_pdb {

    my $line = shift;
    unless ($line) { return; }

    my @words = split(/\|/,$line);
    my $len = @words;

    my $ret = "";
    for (my $i = 0;$i < $len;$i++) {
        if ($words[$i] eq "pdb") {
            if ($i + 1 < $len) {
                $ret .= $words[$i + 1];
                $ret =~ tr/[A-Z]/[a-z]/;
            }
            if ($i + 2 < $len) { 
                $ret .= $words[$i + 2]; 
            } else { 
                $ret .= "_"; 
            }
            return $ret;
        }
    }
    
}


############ insert_chain_data ##############
# Inserts chain data into the chain table   #
# parameters: $pdbChain, $databaseHandle    #
# return: newly generated chain ID          #
#############################################
sub insert_chain_data {

    my ($pdb_chain,$dbh) = @_;
    unless ($dbh) { return; }
    
    # translate all charaters to uppercase
    $pdb_chain =~ tr/a-z/A-Z/;

    my $query = "SELECT chain_id FROM Chain WHERE pdb_chain = \"".$pdb_chain."\";";
    my $sth = $dbh->prepare($query);
    $sth->execute();

    my $chain_id;
    $sth->bind_columns(\$chain_id);
    $sth->fetch();
    $sth->finish();

    if ($chain_id) { return $chain_id; }
    $query = "INSERT INTO Chain (pdb_chain) VALUES (\"".$pdb_chain."\");";
    $dbh->do($query);

    $query = "select last_insert_id();";
    $sth = $dbh->prepare($query);
    $sth->execute();
    $sth->bind_columns(\$chain_id);
    $sth->fetch();
    $sth->finish();

    return $chain_id;
    
}


############# get_file_content ################
# Fetches the content from the supplied file  #
# parameters: $fileName                       #
# return: file content string                 #
###############################################
sub get_file_content {
    my $fName = shift;
    unless (open (FILE,$fName)) {
	    print "Can not open file: ",$fName,"\n";
	    exit;
    }
    my @conetnt = <FILE>;
    return @conetnt;
}