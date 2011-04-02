#!/usr/bin/perl

##############################################################
#                                                            #
# Parses a HMM gene results file generating the amino acid   #
# sequence for the corresponding gene mathces.               #
#                                                            #
# USAGE: parseHMMGene <hmmGeneOutputFile> <contigFastaFile>  #
# Author: Chris Baglieri                                     #
# Bioinformatics Algorithms & Methods                        #
#                                                            #
##############################################################

# grab the hmm gene results file
my ($hmmGeneFile) = $ARGV[0];
chomp $hmmGeneFile;

# grab the contig FASTA file
my ($contigFile) = $ARGV[1];
chomp $contigFile;

# check to see if the specified HMM gene file exists
# if so then continue processing otherwise display
# the appropriate error message
if ( -f $hmmGeneFile && -f $contigFile )  {

    # open the HMM gene output file and extract the 
    # contents into a single variable.Ê close the file 
    # once complete. 
    open(HMMGENEFILE, $hmmGeneFile); 
    my ($hmmGeneData) = join('', <HMMGENEFILE>); 
    close(HMMGENEFILE);
    
    # open the contig FASTA file and extract the 
    # contents into a single variable.Ê close the file 
    # once complete. 
    open(CONTIGFASTA, $contigFile); 
    my ($contigFastaData) = join('', <CONTIGFASTA>); 
    close(CONTIGFASTA);
    
    # extract the contig sequence from the fasta file
    $contigFastaData =~ /^>.*\n([aAcCtTgG\n]*)/;
    my ($contigSequence) = $1;
    $contigSequence =~ s/\n//g;

    # extract the nucleotide sequences given the HMM gene results
    my (@sequenceMatches) = extractSequencesFromHmmGene( $hmmGeneData, $contigSequence );
        
    # print the sequence matches to STDOUT, translating
    # the nucleotide stands to their respective proteins
    foreach $sequenceMatch ( @sequenceMatches ) {
        my ($protein) = translateDNAToProtein($sequenceMatch);
        print "GENE MATCH:\n";
        print "     LENGTH (in residues): " . length $protein;
        print "\n";
        print "     PROTEIN SEQUENCE:\n";
        print $protein;
        print "\n\n";
    }  
    
} else {
        
    # specified HMM gene results or contig file not found, display error message
    print "Specified HMMGene results or contig FASTA file does not exist, please try again.\n";        

}


############## subroutine extractSequencesFromHmmGene ###########
# extracts the nucleotide sequences from a contig given the     #
# results of an HMM Gene process                                #
# USAGE: extractSequencesFromHmmGene( $hmmGeneResults,          #
#                                     $nucleotideSequence )     #
# RETURNS: nucleotide sequence hash                             #
#################################################################
sub extractSequencesFromHmmGene {
    
    # grab the subroutine arguments
    my ($hmmGeneData, $nucleotideSequence) = @_;

    # iterate over each sequence match in the HMM Gene results
    # and add them to a hash that contains each gene identified
    my (@sequenceMatches) = ();
    while ( $hmmGeneData =~ /^# SEQ.*\n(^(?!# SEQ).*\n)+/gm ) {
        
        # iterate over each exon set in the sequence 
        # starting with the first exon of each prediction
        my ($sequenceMatch) = $&;
        while ( $sequenceMatch =~ /^.*firstex.*\n(^(?!.*firstex).*\n)+/gm ) {
            
            my ($sequenceRegion) = $&;
            
            # grab the starting position for the gene
            # this is used to determine if the gene
            # predicted is in the 5' to 3' left to
            # right or right to left direction
            $sequenceRegion =~ /.*firstex[\s]*([0-9]*)/s;
            my ($geneStartPosition) = $1;
            my ($reverseComplementFlag) = 0;
            
            # iterate over each sequence line, processing
            # only the exon entries (e.g. non-CDS items)
            my ($codingSequence);
            while ( $sequenceRegion =~ /.*\n+/gm ) {
                
                my ($sequenceLine) = $&;
                my ($startPosition, $stopPosition);
                
                # retrieve the exon start and stop positions
                if ( $sequenceLine =~ /.*firstex/ ) {
                    ($startPosition, $stopPosition) =
                        ($sequenceLine =~ /.*firstex[\s]*([0-9]*)[\s]*([0-9]*)/);
                } elsif ($sequenceLine =~ /.*exon/ ) { 
                    ($startPosition, $stopPosition) =
                        ($sequenceLine =~ /.*exon_[0-9]*[\s]*([0-9]*)[\s]*([0-9]*)/); 
                } elsif ( $sequenceLine =~ /.*lastex/ ) {
                    ($startPosition, $stopPosition) =
                        ($sequenceLine =~ /.*lastex[\s]*([0-9]*)[\s]*([0-9]*)/);
                }
                     
                # extract the nucleotide segment from the contig
                my ($nucleotideSegment) = substr $nucleotideSequence, 
                    $startPosition-1, 
                    $stopPosition-$startPosition+1;
                
                # check the direction (right to left versus
                # left to right) of the predicted gene region
                # if right to left then set the reverse complement
                # flag
                if ( $reverseComplementFlag != 1 && 
                     $startPosition != "" && 
                     $geneStartPosition > $startPosition ) {
                    $reverseComplementFlag = 1;
                }
                
                # concatenate the nucleotide segment to the coding region
                $codingSequence = $codingSequence . $nucleotideSegment;
                
            } # end while $sequenceRegion
            
            # take the reverse complement the coding sequence if 
            # the region is being 'read' from the right to left
            if ( $reverseComplementFlag == 1 ) {
                $codingSequence = generateReverseComplement($codingSequence);
            }
            
            # add the full coding region to the sequence match hash
            push @sequenceMatches, $codingSequence;

        } # end while $sequenceMatch
        
    } # end while $hmmGeneData

    return @sequenceMatches;

}


######### subroutine generateReverseComplement ##################
# generates the reverse complement of a DNA sequence            #
# USAGE: generateReverseComplement( $dnaSequence )              #
# RETURNS: reverse complement nucleotide string                 #
#################################################################
sub generateReverseComplement {
    my ($dnaSequence) = @_;
    $dnaSequence = join( '', reverse (split(//, $dnaSequence)) );
    $dnaSequence =~ s/A/T/g;
    $dnaSequence =~ s/T/A/g;
    $dnaSequence =~ s/G/C/g;
    $dnaSequence =~ s/C/G/g;
    return $dnaSequence;
}


############## subroutine translateDNAToProtein #################
# translates a sequence of nucleotides to an array of amino     #
# acid residues                                                 #
# USAGE: translateDNAToProtein( $dnaSequence )                  #
# RETURNS: protein sequence string                              #
#################################################################
sub translateDNAToProtein {

    # grab the DNA sequence to translate 
    my ($dnaSequence) = @_; 

    # construct the standard genetic code 
    %standardGeneticCode= ( 
        "TTT"=> "F",    "TCT"=> "S",    "TAT"=> "Y",    "TGT"=> "C",
        "TTC"=> "F",    "TCC"=> "S",    "TAC"=> "Y",    "TGC"=> "C",
        "TTA"=> "L",    "TCA"=> "S",    "TAA"=> "-",    "TGA"=> "-",
        "TTG"=> "L",    "TCG"=> "S",    "TAG"=> "-",    "TGG"=> "W",
        "CTT"=> "L",    "CCT"=> "P",    "CAT"=> "H",    "CGT"=> "R",
        "CTC"=> "L",    "CCC"=> "P",    "CAC"=> "H",    "CGC"=> "R",
        "CTA"=> "L",    "CCA"=> "P",    "CAA"=> "Q",    "CGA"=> "R", 
        "CTG"=> "L",    "CCG"=> "P",    "CAG"=> "Q",    "CGG"=> "R", 
        "ATT"=> "I",    "ACT"=> "T",    "AAT"=> "N",    "AGT"=> "S", 
        "ATC"=> "I",    "ACC"=> "T",    "AAC"=> "N",    "AGC"=> "S", 
        "ATA"=> "I",    "ACA"=> "T",    "AAA"=> "K",    "AGA"=> "R", 
        "ATG"=> "M",    "ACG"=> "T",    "AAG"=> "K",    "AGG"=> "R", 
        "GTT"=> "V",    "GCT"=> "A",    "GAT"=> "D",    "GGT"=> "G", 
        "GTC"=> "V",    "GCC"=> "A",    "GAC"=> "D",    "GGC"=> "G", 
        "GTA"=> "V",    "GCA"=> "A",    "GAA"=> "E",    "GGA"=> "G", 
        "GTG"=> "V",    "GCG"=> "A",    "GAG"=> "E",    "GGG"=> "G" 
    );

    # process the DNA sequence iterating over the set 
    # of codons translating each to the appropriate 
    # amino acid residue 
    my (@triplets) = unpack("a3" x (length($ dnaSequence )/3), $ dnaSequence ); 
    my ($proteinSequence);
    foreach $codon (@triplets) { 
        $proteinSequence = $proteinSequence. $standardGeneticCode{$codon}; 
    }

    # return the amino acid sequence 
    return $proteinSequence; 

}