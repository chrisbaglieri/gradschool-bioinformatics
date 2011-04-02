#!/usr/bin/perl
########################################################## 
# DESCRIPTION:                                           # 
# Generates a statistical profile matrix and             # 
# overall alignment profile for a CLUSTAL alignment      # 
# output file.                                           # 
#                                                        # 
# Usage: generateProfile <alignmentFile.aln>             # 
# Author: Chris Baglieri                                 # 
# Bioinformatics Algorithms and Methods                  # 
########################################################## 

my ($alignmentFile) = $ARGV[0]; 
chomp $alignmentFile; 

# check to see if the specified alignment file exists 
# if so then continue processing otherwise display 
# the appropriate error message and script usage 
if ( -f $alignmentFile )  {

    # open the CLUSTAL alignment file and extract the 
    # contents into a single variable.  close the file 
    # once complete. 
    open(ALIGNMENTFILE, $alignmentFile); 
    my ($alignmentData) = join('', <ALIGNMENTFILE>); 
    close(ALIGNMENTFILE);

    # print the output header 
    &printOutputHeader;
    
    # determine the number of aligned sequences 
    my ($numberOfAlignedSequences) = calculateNumberOfSequences( $alignmentData ); 
    
    # concatenate the sequences for each alignment 
    my (@alignedSequenceStrings) = generateSequenceStrings( $alignmentData, $numberOfAlignedSequences );
    
    # translate each aligned sequence string to
    # an array of characters so we can iterate 
    # over each position more efficiently 
    my (@alignedSequenceCharacters) = (); 
    my ($sequenceCharacterIndex) = 0; 
    foreach my $sequence (@alignedSequenceStrings) { 
        my (@sequenceCharacterArray) = split( //, $sequence );
        $alignedSequenceCharacters[$sequenceCharacterIndex++] = \@sequenceCharacterArray; 
    }

    # iterate over each position in the aligned sequences.  for each, 
    # calculate the amino acid frequencies, and display the results. 
    my ($residuePosition) = 0; 
    my ($totalNumberOfPositions) = length $alignedSequenceStrings[0]; 
    while ( $residuePosition != -1 ) { 

        # generate an empty profile for this position 
        my (%profile) = &generateProfileHash;

        # iterate over each of the sequence positions
        # incrementing the appropriate reside count
        # this will produce a row in the statistical
        # profile matrix.
        my ($sequenceIndex) = 0; 
        for ( $sequenceIndex = 0; $sequenceIndex < $numberOfAlignedSequences; ++$sequenceIndex ) { 
            
            # check for mutations (e.g. the '-' character
            # in the sequence alignment).  note that skipping
            # '-' entries is accounted for in the frequencey 
            # values calculated for the profile
            if ( $alignedSequenceCharacters[$sequenceIndex]->[$residuePosition] =~ /[^-]/ ) {
                $profile{$alignedSequenceCharacters[$sequenceIndex]->[$residuePosition]}++;
            }
            
        }
                
        # translate the amino acid counts to frequency
        %profile = translateResidueCountToFrequencies( %profile ); 

        # output the particular residue position profile
        # note that since the residue position is used
        # as an index into the profile, it is zero-based.
        # increment the position number for formatting
        # purposes.
        printProfileHash ( $residuePosition+1, %profile ); 

        # advance to the next position in the sequence and 
        # check if the previous was the last residue.  if 
        # so then assign the position the exit value of -1 
        if ( ++$residuePosition == $totalNumberOfPositions ) { 
            $residuePosition = -1; 
        }

    }
    
} else { 

    # specified CLUSTAL alignment file not found, display error message 
    print "Specified CLUSTAL alignment file does not exist, please try again.\n"; 
    print "Usage: generateProfile <alignmentFile.aln>\n"; 

}


############## subroutine generateSequenceStrings #############
#                                                             #  
# generates an array of sequence strings given the data       # 
# from a CLUSTAL alignment output file                        # 
# usage: generateSequenceStrings ( alignmentData, seqCount )  # 
# returns: array with the set of sequence strings             #
#                                                             #  
############################################################### 
sub generateSequenceStrings { 
    
    # declare local contstants
    my ($GI_TAG_HEADER_LENGTH) = 34;
    my ($SEQUENCE_LENGTH) = 50;

    # declare local variables
    my ($alignmentData, $numberOfSequences) = @_; 
    my (@sequenceStrings) = ();
    my ($sequenceIndex) = $numberOfSequences;
    
    # iterate over each sequence block
    while ( $alignmentData =~ /^gi.*\n(^(?=gi).*\n)+/gm ) {
        
        my ($sequenceBlock) = $&;

        # iterate over each sequence and concatenate
        # the residues adding the item to the
        # sequence string array
        while ( $sequenceBlock =~ /^gi.*\n/gm ) {
            my ($sequence) = $&;
            $sequence =~ /^(gi.{$GI_TAG_HEADER_LENGTH})([ACDEFGHIKLMNPQRSTVWY\-]{$SEQUENCE_LENGTH})/;
            $sequenceStrings[($numberOfSequences - $sequenceIndex--)] .= $2;
        }
        
        # reset the sequence index to the number of sequences
        $sequenceIndex = $numberOfSequences;
        
    }
    
    return @sequenceStrings; 

}


######## subroutine translateResidueCountToFrequencies ########
#                                                             # 
# calculates the frequences for a given residue profile       # 
# usage: calculateResidueFrequencies ( %profile )             # 
# returns: hash containing the frequency profile              #
#                                                             #
############################################################### 
sub translateResidueCountToFrequencies { 

    my (%countProfile) = @_; 
    my (%frequenceyProfile) = &generateProfileHash; 
    
    # iterate over each amino acid in the count profile 
    # to get the total number of "matched residues"
    # (e.g. accounting for the mismatch character '-')
    my ($totalCount) = 0;
    foreach my $residueCount ( values(%countProfile) ) {
        $totalCount += $residueCount;
    } 
    
    # generate the frequency profile hash. note that
    # the frequence is represented by a decimal value
    # with a precision of two.
    $frequencyProfile{"A"} = sprintf( "%.2f", ($countProfile{"A"} / $totalCount) ); 
    $frequencyProfile{"R"} = sprintf( "%.2f", ($countProfile{"R"} / $totalCount) );
    $frequencyProfile{"N"} = sprintf( "%.2f", ($countProfile{"N"} / $totalCount) );
    $frequencyProfile{"D"} = sprintf( "%.2f", ($countProfile{"D"} / $totalCount) );
    $frequencyProfile{"C"} = sprintf( "%.2f", ($countProfile{"C"} / $totalCount) );
    $frequencyProfile{"Q"} = sprintf( "%.2f", ($countProfile{"Q"} / $totalCount) );
    $frequencyProfile{"E"} = sprintf( "%.2f", ($countProfile{"E"} / $totalCount) );
    $frequencyProfile{"G"} = sprintf( "%.2f", ($countProfile{"G"} / $totalCount) );
    $frequencyProfile{"H"} = sprintf( "%.2f", ($countProfile{"H"} / $totalCount) );
    $frequencyProfile{"I"} = sprintf( "%.2f", ($countProfile{"I"} / $totalCount) );
    $frequencyProfile{"L"} = sprintf( "%.2f", ($countProfile{"L"} / $totalCount) );
    $frequencyProfile{"K"} = sprintf( "%.2f", ($countProfile{"K"} / $totalCount) );
    $frequencyProfile{"M"} = sprintf( "%.2f", ($countProfile{"M"} / $totalCount) );
    $frequencyProfile{"F"} = sprintf( "%.2f", ($countProfile{"F"} / $totalCount) );
    $frequencyProfile{"P"} = sprintf( "%.2f", ($countProfile{"P"} / $totalCount) );
    $frequencyProfile{"S"} = sprintf( "%.2f", ($countProfile{"S"} / $totalCount) );
    $frequencyProfile{"T"} = sprintf( "%.2f", ($countProfile{"T"} / $totalCount) );
    $frequencyProfile{"W"} = sprintf( "%.2f", ($countProfile{"W"} / $totalCount) );
    $frequencyProfile{"Y"} = sprintf( "%.2f", ($countProfile{"Y"} / $totalCount) );
    $frequencyProfile{"V"} = sprintf( "%.2f", ($countProfile{"V"} / $totalCount) );
    
    return %frequencyProfile; 

}


############ subroutine calculateNumberOfSequences ############
#                                                             # 
# calculates the number of aligned sequences processed        # 
# in a CLUSTAL alignment output file                          # 
# usage: calculateNumberOfSequences ( alignmentData )         # 
# returns: the number of aligned sequences                    #
#                                                             #  
############################################################### 
sub calculateNumberOfSequences {
    
    my ($alignments) = @_;
    
    # grab a sequence block from the alignment data
    $alignments =~ /^gi.*\n(^(?=gi).*\n)+/gm;
    my ($sequenceBlock) = $&;
    
    # count the number of sequences in the block
    my ($sequenceCount) = 0;
    while ( $sequenceBlock =~ /^gi.*\n/gm ) {
        ++$sequenceCount;
    }
    
    return $sequenceCount;
    
}


################ subroutine generateProfileHash ############### 
#                                                             # 
# subroutine that generates an empty profile hash for the     # 
# the set of amino acid residues                              # 
# usage: &generateProfileHash                                 # 
# returns: defaulted amino acid profile hash                  #
#                                                             # 
############################################################### 
sub generateProfileHash { 
    
    # declare local constants
    my ($DEFAULT_PROFILE_VALUE) = 0;
    
    # declare local variables
    my (%profileHash) = ();
    
    # add each amino acid residue with the default value of 0 
    $profileHash{"A"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"R"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"N"} = $DEFAULT_PROFILE_VALUE;
    $profileHash{"D"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"C"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"Q"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"E"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"G"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"H"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"I"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"L"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"K"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"M"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"F"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"P"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"S"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"T"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"W"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"Y"} = $DEFAULT_PROFILE_VALUE; 
    $profileHash{"V"} = $DEFAULT_PROFILE_VALUE; 
    
    return %profileHash; 
    
}


################ subroutine printProfileHash ##################
#                                                             #  
# subroutine that sends a sequence's profile to STDOUT        # 
# usage: printProfileHash( $position, %profileHash )          # 
# returns: void                                               #
#                                                             #  
############################################################### 
sub printProfileHash { 

    # grab the arguments to the subroutine 
    my ($position, %profile) = @_; 

    print "$position:\t"; 
    print $profile{"A"} . " "; 
    print $profile{"R"} . " "; 
    print $profile{"N"} . " "; 
    print $profile{"D"} . " "; 
    print $profile{"C"} . " "; 
    print $profile{"Q"} . " "; 
    print $profile{"E"} . " "; 
    print $profile{"G"} . " "; 
    print $profile{"H"} . " "; 
    print $profile{"I"} . " "; 
    print $profile{"L"} . " "; 
    print $profile{"K"} . " "; 
    print $profile{"M"} . " "; 
    print $profile{"F"} . " "; 
    print $profile{"P"} . " "; 
    print $profile{"S"} . " "; 
    print $profile{"T"} . " "; 
    print $profile{"W"} . " "; 
    print $profile{"Y"} . " "; 
    print $profile{"V"} . " "; 
    print "\n"; 
    
}


################ subroutine printOutputHeader ################# 
#                                                             # 
# subroutine that prints the header for the results of this   # 
# script to STDOUT                                            # 
# usage: &printOutputHeader                                   # 
# returns: void                                               #
#                                                             #  
############################################################### 
sub printOutputHeader { 
    print "\nSTATISTICAL PROFILE:\n\n"; 
    print "\tA    "; 
    print "R    "; 
    print "N    "; 
    print "D    "; 
    print "C    "; 
    print "Q    "; 
    print "E    "; 
    print "G    "; 
    print "H    "; 
    print "I    "; 
    print "L    "; 
    print "K    "; 
    print "M    "; 
    print "F    "; 
    print "P    "; 
    print "S    "; 
    print "T    "; 
    print "W    "; 
    print "Y    "; 
    print "V    "; 
    print "\n\n"; 
}