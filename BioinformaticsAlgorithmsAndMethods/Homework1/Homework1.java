

/**
 * Given a FASTA nucleotide sequence file, this
 * class generates six amino acid sequences.  The
 * first three are generated from the phases 0,
 * 1, and 2 of the FASTA nucleotide sequence.
 * The remaining sequences are generated from
 * phases 0, 1, and 2 of the compliment FASTA
 * nucleotide sequence.
 *
 * All sequences are sent to the console.  The
 * usage syntax follows:
 *
 * Homework1 <fullyQualifiedFASTASequenceFilename>
 *
 */
public class Homework1 {
    
    /**
     * Command line entry point to the amino
     * acid generator class.
     *
     * @param args Command line arguments
     */
    public static void main( String[] args ) {
        
        try {
            
            // validate that a FASTA file was provided
            if ( args.length == 0 ) {
                System.out.println("FASTA sequence file was not provided.");
                System.out.println("USAGE java Homework1 <FASTAFilename>");
                return;
            }
        
            // instantiate our genetic code translator
            GeneticCodeTranslator geneticCodeTranslator = 
                new GeneticCodeTranslator();
            
            // instantiate the FASTA sequence file
            FastaBaseSequenceFile sequenceFile = 
                new FastaBaseSequenceFile( args[0] );
            
            // sequence #1: non-compliment phase 0
            sequenceFile.initialize();
            System.out.println("");
            System.out.println("SEQUENCE #1: NON-COMPLIMENT PHASE 0"); 
            System.out.println( Homework1.
                generateProtein(sequenceFile, geneticCodeTranslator) ); 
            System.out.println("");
            
            // sequence #2: non-compliment phase 1
            sequenceFile.initialize(1);
            System.out.println("SEQUENCE #2: NON-COMPLIMENT PHASE 1");
            System.out.println( Homework1.
                generateProtein(sequenceFile, geneticCodeTranslator) ); 
            System.out.println("");
            
            // sequence #3: non-compliment phase 2
            sequenceFile.initialize(2);
            System.out.println("SEQUENCE #3: NON-COMPLIMENT PHASE 2");
            System.out.println( Homework1.
                generateProtein(sequenceFile, geneticCodeTranslator) ); 
            System.out.println("");
            
            // sequence #4: compliment phase 0
            sequenceFile.initialize();
            sequenceFile.complimentSequence();
            System.out.println("SEQUENCE #4: COMPLIMENT PHASE 0");
            System.out.println( Homework1.
                generateProtein(sequenceFile, geneticCodeTranslator) ); 
            System.out.println("");
            
            // sequence #5: compliment phase 1
            System.out.println("SEQUENCE #5: COMPLIMENT PHASE 1");
            sequenceFile.initialize(1);
            sequenceFile.complimentSequence();
            System.out.println( Homework1.
                generateProtein(sequenceFile, geneticCodeTranslator) ); 
            System.out.println("");
            
            // sequence #6: compliment phase 2
            System.out.println("SEQUENCE #6: COMPLIMENT PHASE 2");
            sequenceFile.initialize(2);
            sequenceFile.complimentSequence();
            System.out.println( Homework1.
                generateProtein(sequenceFile, geneticCodeTranslator) ); 
            System.out.println("");
            
        } catch (Exception ex) {
            System.out.println("");
            System.out.println("ERROR ENCOUNTERED:");
            System.out.println( ex.getMessage() );
        }
        
    }
    
    private static String generateProtein( FastaBaseSequenceFile sequenceFile,
        GeneticCodeTranslator translator) throws Exception {
        
        StringBuffer aminoAcidSequence = new StringBuffer();
        String codon = sequenceFile.readCodon();
        
        // read each codon in the FASTA sequence file
        // and generate the appropriate amino acids
        while( codon != null ) {
            aminoAcidSequence.append( translator.
                generateAminoAcid( codon.charAt(0),
                codon.charAt(1),
                codon.charAt(2) ) );
            codon = sequenceFile.readCodon();
        }
        
        return aminoAcidSequence.toString();

    }
        
}