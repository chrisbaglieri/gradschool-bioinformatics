#!/usr/bin/perl
##############################################################
# Script that generates an annotated k means algorithm       #
# results file.  Usage follows:                              #
# annotateKMeansResults <algorithmResultsFile>  <goFile>     #
##############################################################

# define constants
$USAGE = "annotateKMeansResults <algorithmResultsFile> <goFile>";
$OUTPUT_FILE = "annotatedAlgorithmResults";

$algorithmResultsFile = $ARGV[0];
$goFile = $ARGV[1];
chomp $algorithmResultsFile;
chomp $goFile;

# check to see if the specified files exist
# if so then continue processing otherwise display
# the appropriate error message
if ( -f $algorithmResultsFile && -f $goFile )  {
    
    # open up the output file.  note that the
    # contents of the file will be overwritten 
    # (e.g. using '>' when opening the file)
    open(OUTPUTFILE, ">$OUTPUT_FILE");
    
    # write the output file header
    print OUTPUTFILE &writeOutputFileHeader;
    
    # open the algorithm results file
    open(ALGORITHM_RESULTS, $algorithmResultsFile);
    
    # iterate over the lines of the algorithm results
    # note that the performance for this iteration can
    # be improved upon.  instead of constantly hitting
    # the disk, the contents of the results file can
    # be read into a single variable.
    until ( eof(ALGORITHM_RESULTS) ) { 
        
        $resultsEntry = <ALGORITHM_RESULTS>;

        # check if this is a new cluster entry. if
        # so output the appropriate header information
        # otherwise, annotate the cluster data point
        if ( checkForNewCluster($resultsEntry) == 1 ) {
            print OUTPUTFILE "\n\n";
            print OUTPUTFILE $resultsEntry;
            print OUTPUTFILE &writeClusterHeader;
        } elsif ( isHeaderDelimeter($resultsEntry) == 0 ) {
            print OUTPUTFILE annotateResults( $resultsEntry );
        }
        
    }
    
    # close the algorithm results file
    close(ALGORITHM_RESULTS);
   
    # close the output file
    close(OUTPUTFILE);
    
} else {
    
    print "Either the algorithm results or GO file specified does not exist, please try again.\n";        

}

############## subroutine writeOutputFileHeader ###############
# generates the header for the output file                    #
###############################################################
sub writeOutputFileHeader {
   return "Annotated Cluster Report\n" .
    "Chris baglieri\n" .
    "Computational Systems Biology\n\n";
}


############## subroutine writeClusterHeader ##################
# generates the cluster header for the output file            #
###############################################################
sub writeClusterHeader {
   return "\nAccession\t" .
    "Gene Name\t" .
    "Description\t" .
    "Function\t" .
    "Location\t" .
    "Process\n";
}


############## subroutine checkForNewCluster ##################
# checks for a new cluster entry                              #
###############################################################
sub checkForNewCluster {
    my ($clusterEntry) = @_;
    if( $clusterEntry =~ /^CLUSTER DETAILS/ ) {
        return 1;
    }
    return 0;
}

############## subroutine isHeaderDelimeter ###################
# checks for a header delimeter                               #
###############################################################
sub isHeaderDelimeter {
    my ($clusterEntry) = @_;
    if( $clusterEntry =~ /^##########/ || length ($clusterEntry) == 1 ) {
        return 1;
    }
    return 0;
}


################# subroutine annotateResults ##################
# subroutine that iterates over the algorithm results file    #
# annotating each expression data point within the clusters   #
# usage: annotateResults resultsData                          #
###############################################################
sub annotateResults {
    
    my ($expressionDataPoint) = @_;
    my (@dataPointParts) = split( / /, $expressionDataPoint );
    
    # move to the particular GO entry
    return extractOntologyData( $dataPointParts[0] );
        
}

################# subroutine extractOntologyData ##############
# subroutine that extracts the ontology information for a     #
# specified gene.  note that this method can be improved      #
# instead of constantly using IO the contents of the GO file  #
# can be read into a single variable.                         #
# usage: extractOntologyData geneName                         #
###############################################################
sub extractOntologyData {
    
    my ($gene) = @_;
    my $ontologyDataResults = "$gene\tNo GO data found\n";
    my $goFileEntry;
    my @goFileEntryParts;

    # open the GO file
    open(GO_FILE, $goFile);

    # iterate over the genes and find the match    
    until ( eof(GO_FILE) ) {
        
        $goFileEntry = <GO_FILE>;
        @goFileEntryParts = split( /\t/, $goFileEntry );
        
        # construct the ontologyDataResults
        if ( $goFileEntryParts[0] eq $gene ) {
            $ontologyDataResults = "$goFileEntryParts[0]\t" .
                "$goFileEntryParts[1]\t" .
                "$goFileEntryParts[3]\t" .
                "$goFileEntryParts[4]\t" .
                "$goFileEntryParts[5]\n";
            last;
        }
       
    }
    
    # close the algorithm results and GO files
    close(GO_FILE);
    return $ontologyDataResults;
   
}