#!/usr/bin/perl

##############################################################
#                                                            #
# Script that parses a HMMER search results file into a      #
# a tab delimated list detailing the characteristics of      #
# matched sequences.                                         #
#                                                            #
# USAGE: parseHMMER <hmmSearchResultsFile>                   #
# Author: Chris Baglieri                                     #
# Bioinformatics Algorithms & Methods                        #
#                                                            #
##############################################################

# declare hmmer results region REGEX constants
$SEQUENCE_REGEX = "^Scores for complete sequences";
$DOMAIN_REGEX = "^Parsed for domains:\n";
$ALIGNMENT_REGEX = "^Alignments of top-scoring domains:\n";
$HISTOGRAM_REGEX = "^Histogram of all scores:";

# grab the search results filename
$hmmerSearchFile = $ARGV[0];
chomp $hmmerSearchFile;

# check to see if the specified HMMER file exists
# if so then continue processing otherwise display
# the appropriate error message
if ( -f $hmmerSearchFile )  {
            
    # generate the alignment details output filename
    $outputFilename = "hmmerSearchDetails-" . $hmmerSearchFile;
    
    # if the output file already exists, rename the
    # original appending ".old".  this is done to
    # prevent the user from accidently deleting an
    # output file generated from a previous run
    if ( -f $outputFilename ) {
        print "Output file already exists, appending existing file with '.old' before processing new request!\n";
        rename $outputFilename, $outputFilename . ".old";
    }
    
    # open up the output file.  note that the
    # contents of the file will be overwritten 
    # (e.g. using '>' when opening the file)
    open(OUTPUT_FILE, ">$outputFilename");
    
    # write the output file header
    &writeOutputFileHeader;
    
    # open the HMMER search results file
    open(HMMER_SEARCH_FILE, $hmmerSearchFile);
    
    # extract the contents of the file into a single variable
    $hmmerSearchData = join('', <HMMER_SEARCH_FILE>);
    
    # close the HMMER search results file
    close(HMMER_SEARCH_FILE);
    
    # extract the different sections from the HMMER search results
    ($sequenceScores, $hmmerDomains, $hmmerAlignments, $histogram) = 
        ($hmmerSearchData =~/($SEQUENCE_REGEX.*)($DOMAIN_REGEX.*)($ALIGNMENT_REGEX.*)($HISTOGRAM_REGEX.*)/ms);
    
    # parse the HMMER alignments section
    parseHmmerAlignments( $hmmerAlignments );
    
    # close the output file
    close(OUTPUT_FILE);
    
} else {
        
    # specified HMMER search results file not found, display error message
    print "Specified HMMER search results file does not exist, please try again.\n";        

}


############## subroutine parseHmmerAlignments ################
# subroutine that parses HMMER alignments into an tab         #
# seperated list file.  the items included follow:            #
#                                                             #
#    - HMM sequence header                                    #
#    - alignment length                                       #
#    - score (bits)                                           #
#    - expected value                                         #
#                                                             #
# USAGE: parseHmmerAlignments( $alignments )                  #
###############################################################
sub parseHmmerAlignments {
    
    my ($alignments) = @_;
    my (%alignmentDetailsHash) = ();
    
    # remove the header line from the alignments section
    ($alignments) = ($alignments =~ /$ALIGNMENT_REGEX(.*)/s);

    # iterate over each alignment and extract
    # the desired alignment details into a hash.
    # for each alignment, append a new line item 
    # in the tab delimited file
    while( $alignments =~ /^gi.*\n(^(?!gi).*\n)+/gm) {
        
        # grab the alignment data
        my ($alignment) = $&;

        ## parse the HMM sequence header
        my ($header ) = ($alignment =~ /^([a-zA-Z|0-9]*)/);
        $alignmentDetailsHash{"header"} = $header;
        
        # parse the alignment length
        my ($start, $end ) = ($alignment =~ /^.*from ([0-9]*) to ([0-9]*)/);
        $alignmentDetailsHash{"length"} = $end - $start;
        
        # parse the score
        my ($score ) = ($alignment =~ /^.*score ([\-0-9]*\.[0-9]*)/);
        $alignmentDetailsHash{"score"} = $score;
        
        # parse the e-value
        my ($eValue ) = ($alignment =~ /^.*E = ([0-9]*\.?e?-?[0-9]*)/);
        $alignmentDetailsHash{"eValue"} = $eValue;
        
        # write the contents of the alignment hash to file
        writeAlignmentEntry( %alignmentDetailsHash );
        
    } # END alignment while
        
}


############## subroutine writeOutputFileHeader ###############
# writes the output file header to the alignment details      #
# output file                                                 #
# USAGE: &writeOutputFileheader                               #
###############################################################
sub writeOutputFileHeader {
   print OUTPUT_FILE "HMM Sequence Tag\t";
   print OUTPUT_FILE "Length\t";
   print OUTPUT_FILE "Score\t";
   print OUTPUT_FILE "Expected Value\n";
}


############## subroutine writeAlignmentEntry #################
# writes the set of alignment details to output file          #
# USAGE: writeAlignmentEntry( %alignmentEntry )               #
###############################################################
sub writeAlignmentEntry {
   my (%alginmentEntry) = @_;
   print OUTPUT_FILE $alginmentEntry{"header"} . "\t";
   print OUTPUT_FILE $alginmentEntry{"length"} . "\t";
   print OUTPUT_FILE $alginmentEntry{"score"} . "\t";
   print OUTPUT_FILE $alginmentEntry{"eValue"} . "\n";
}