#!/usr/bin/perl
##############################################################
# Script that parses the cluster data from the results of    #
# K Means clustering implementation into SIF format,         #
# consumable by Cytoscape.  Usage follows:                   #
# parseClusterDataToSIF <ppiDataFile> <clusterDataFile>      #
##############################################################

# define constants
$USAGE = "parseClusterDataToSIF <ppiDataFile> <clusterDataFile>";
$OUTPUT_FILE = "clusterData.sif";

$ppiDataFile = $ARGV[0];
$clusterDataFile = $ARGV[1];
chomp $ppiDataFile;
chomp $clusterDataFile;

# check to see if the specified file exists
# if so then continue processing otherwise display
# the appropriate error message
if ( -f $ppiDataFile && -f $clusterDataFile )  {
    
    # open up the output file.  note that the
    # contents of the file will be overwritten 
    # (e.g. using '>' when opening the file)
    open(OUTPUTFILE, ">$OUTPUT_FILE");
    
    # open the PPI Data file
    open(PPI_DATA, $ppiDataFile);
    
    # read the PPI Data into a single variable
    my ($ppiFileContents) = join('', <PPI_DATA>); 
    
    # close the PPI Data file
    close(PPI_DATA);
    
    # open the cluster data file
    open(CLUSTER_DATA, $clusterDataFile);
    
    # read the cluster data into a single variable
    my ($clusterData) = join('', <CLUSTER_DATA>); 
    
    # close the cluster data file
    close(CLUSTER_DATA);
    
    # process the cluster data against the PPI data
    processCluster( $clusterData, $ppiFileContents );
    
    # close the output file
    close(OUTPUTFILE);
    
} else {
    
    print "Either the PPI Data or cluster data files specified does not exist, please try again.\n";
    print "$USAGE\n";
}


################# subroutine processCluster ###################
# subroutine that processes a given cluster data block into   #
# SIF format                                                  #
# usage: processCluster clusterData ppiData                   #
###############################################################
sub processCluster {
    
    my ($clusterData, $ppiData) = @_;
    
    # iterate over each gene that belongs to this cluster
    # and extract the PPI data writing the results to file
    # note this process can be improved since our split
    # does not validate that this is even clustered data.
    # perhaps a validation of the data is required.
    foreach $geneEntry ( split( /\n/, $clusterData) ) {
        @geneEntryParts = split( /\s/, $geneEntry );
        print OUTPUTFILE extractPPIData( $geneEntryParts[0], $ppiData );
    } 
            
}

################## subroutine extractPPIData ##################
# subroutine that extracts the PPI Data for a specified gene. #
# usage: extractPPIData geneName ppiData                      #
###############################################################
sub extractPPIData {
    
    my ($gene, $ppiData) = @_;
    my ($sifData);
    my ($ppiEntry);
    my (@ppiEntryItems);
    
    # iterate over the PPI Data for the set of interactions
    # constructing the proper SIF data entry for this protein
    foreach $ppiEntry ( split(/\n/, $ppiData) ) {

        # check for a match to the gene.  if so,
        # iterate over the next few entries to
        # collect any additional PPI's
        @ppiEntryItems = split( /\t/, $ppiEntry );
        if ( uc($ppiEntryItems[0]) eq $gene ) {
            $sifData .= $gene . " pp " . $ppiEntryItems[3] . "\n";
        }
        
    }
    
    return $sifData;
   
}