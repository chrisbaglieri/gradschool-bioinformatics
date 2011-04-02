/**
 * Class that provides numerous utilities for 
 * translating genetic code fragments.  Currently, 
 * the class supports codon to amino acid
 * translation.
 */
public class GeneticCodeTranslator {
        
    /**
    * Private translation table member
    */
    private String [][][] geneticCode;
    
    /**
     * Constructs a new genetic code translator
     * intializing the genetic code translation
     * table.
     */
    public GeneticCodeTranslator() {
        this.geneticCode = new String [4][4][4];
        this.configureTranslationTable( this.geneticCode );
    }

    /**
     * Generates the appropriate single letter
     * amino acid symbol given the codon sequence.
     *
     * @param baseOne First base in a codon
     * @param baseTwo Second base in a codon
     * @param baseThree Third base in a codon
     * @return Single letter amino acid symbol
     */
    public String generateAminoAcid( char baseOne,
        char baseTwo,
        char baseThree ) throws Exception {
            
        // translate the bases into their base integers
        int baseOneInteger = this.getBaseInteger( baseOne );
        int baseTwoInteger = this.getBaseInteger( baseTwo );
        int baseThreeInteger = this.getBaseInteger( baseThree );
        
        // check to make sure that all bases were valid
        // if not then throw an exception
        if ( (baseOneInteger == -1) || 
             (baseTwoInteger == -1) || 
             (baseThreeInteger == -1) ) {
            throw new Exception( "Unknown base encountered. Unable to generate amino acid." );
        }
            
        return this.geneticCode[baseOneInteger][baseTwoInteger][baseThreeInteger];

    }
    
    /** 
     * Converts a base (A, C, G, U, or T) to 
     * its integer representation.
     *
     * @param base Base character
     * @return Base integer representation
     */
    private int getBaseInteger( char base ) {
        if ((base == 'G') || (base == 'g'))
            return 0;
        else if ( (base == 'A') || (base == 'a') )
            return 1;
        else if ( (base == 'C') || (base == 'c') )
            return 2;
        else if ( (base == 'T') || (base == 't') )
            return 3;
        else if ( (base == 'U') || (base == 'u') )
            return 3;
        else return -1;
    }
    
    /**
     * Configures a genetic code table with the codon alphabet
     *
     * @param geneticCode table containing codons and 
     *        their amino acid single letter sybmbols
     */
    private void configureTranslationTable( String[][][] geneticCode ) {
        
        // glycine
        String glycine = "G";
        geneticCode [0][0][0] = glycine;
        geneticCode [0][0][1] = glycine;
        geneticCode [0][0][2] = glycine;
        geneticCode [0][0][3] = glycine;
        
        // glutamic acid
        String glutamicAcid = "E";
        geneticCode [0][1][0] = glutamicAcid;
        geneticCode [0][1][1] = glutamicAcid;
        
        // alanine
        String alanine = "A";
        geneticCode [0][2][0] = alanine;
        geneticCode [0][2][1] = alanine;
        geneticCode [0][2][2] = alanine;
        geneticCode [0][2][3] = alanine;
        
        // valine
        String valine = "V";
        geneticCode [0][3][0] = valine;
        geneticCode [0][3][1] = valine;
        geneticCode [0][3][2] = valine;
        geneticCode [0][3][3] = valine;
        
        // aspartic acid
        String asparticAcid = "D";
        geneticCode [0][1][2] = asparticAcid;
        geneticCode [0][1][3] = asparticAcid;
        
        // arginine
        String arginine = "R";
        geneticCode [1][0][0] = arginine;
        geneticCode [1][0][1] = arginine;
        geneticCode [2][0][0] = arginine;
        geneticCode [2][0][1] = arginine;
        geneticCode [2][0][2] = arginine;
        geneticCode [2][0][3] = arginine;
        
        // lysine
        String lysine = "K";
        geneticCode [1][1][0] = lysine;
        geneticCode [1][1][1] = lysine;
        
        // threonine
        String threonine = "T";
        geneticCode [1][2][0] = threonine;
        geneticCode [1][2][1] = threonine;
        geneticCode [1][2][2] = threonine;
        geneticCode [1][2][3] = threonine;
        
        // methionine
        String methionine = "M";
        geneticCode [1][3][0] = methionine;
        
        // isoleucine
        String isoleucine = "I";
        geneticCode [1][3][1] = isoleucine;
        geneticCode [1][3][2] = isoleucine;
        geneticCode [1][3][3] = isoleucine;
        
        // luecine
        String leucine = "L";
        geneticCode [2][3][0] = leucine;
        geneticCode [2][3][1] = leucine;
        geneticCode [2][3][2] = leucine;
        geneticCode [2][3][3] = leucine;
        geneticCode [3][3][0] = leucine;
        geneticCode [3][3][1] = leucine;
        
        // serine
        String serine = "S";
        geneticCode [1][0][2] = serine;
        geneticCode [1][0][3] = serine;
        geneticCode [3][2][0] = serine;
        geneticCode [3][2][1] = serine;
        geneticCode [3][2][2] = serine;
        geneticCode [3][2][3] = serine;
        
        // asparagine
        String asparagine = "N";
        geneticCode [1][1][2] = asparagine;
        geneticCode [1][1][3] = asparagine;
        
        // glutamine
        String glutamine = "Q";
        geneticCode [2][1][0] = glutamine;
        geneticCode [2][1][1] = glutamine;
        
        // proline
        String proline = "P";
        geneticCode [2][2][0] = proline;
        geneticCode [2][2][1] = proline;
        geneticCode [2][2][2] = proline;
        geneticCode [2][2][3] = proline;
        
        // histidine
        String histidine = "H";
        geneticCode [2][1][2] = histidine;
        geneticCode [2][1][3] = histidine;
        
        // tryptophan
        String tryptophan ="W";
        geneticCode [3][0][0] = tryptophan;
        
        // cysteine
        String cysteine = "C";
        geneticCode [3][0][2] = cysteine;
        geneticCode [3][0][3] = cysteine;
        
        // tyrosine
        String tyrosine = "Y";
        geneticCode [3][1][2] = tyrosine;
        geneticCode [3][1][3] = tyrosine;
        
        // phenylalanine
        String phenylalanine = "F";
        geneticCode [3][3][2] = phenylalanine;
        geneticCode [3][3][3] = phenylalanine;
        
        // STOP codons
        String stopCodon = "stop";
        geneticCode [3][0][1] = stopCodon;
        geneticCode [3][1][0] = stopCodon;
        geneticCode [3][1][1] = stopCodon;
        
    }
        
}