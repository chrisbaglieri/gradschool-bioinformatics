#!/usr/bin/perl
##############################################################
# Script that parses a file containing numerous FASTA        #
# outputs from a given search, outputting the set of         #
# sequence species.                                          #
##############################################################

$fastaFile = $ARGV[0];
chomp $fastaFile;

# check to see if the specified FASTA file exists
# if so then continue processing otherwise display
# the appropriate error message
if ( -f $fastaFile )  {
        
    # open the FASTA file
    open(FASTAFILE, $fastaFile);
        
    # iterate over each line in the file until the end
    # is reached. if the fasta line item is a header 
    # line then, extract the species.
    until ( eof(FASTAFILE) ) { 
        $fastaFileLine = <FASTAFILE>;
        if ( &isFastaHeader ) {
            print &extractSpecies;
            print "\n";
        }
    }
    
    # close the FASTA file
    close(FASTAFILE);

} else {
        
    # specified FASTA file not found, display error message
    print "Specified FASTA output file does not exist, please try again.\n";        

}

################## subroutine isFastaHeader ###################
# subroutine that checks a fasta line item to see if it is a  #
# header file or not using regular expressions                #
###############################################################
sub isFastaHeader {
    if( $fastaFileLine =~ /^>/ ) {
        return 1;
    }
    return 0;
}

################## subroutine extractSpecies ###################
# subroutine that extracts the species name from a fasta file  #
# header using regular expressions                             #
################################################################
sub extractSpecies {
    $fastaFileLine =~ /\[(.*)\]/;
    return $1;
}