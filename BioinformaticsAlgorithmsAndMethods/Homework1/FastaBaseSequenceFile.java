import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class that wraps a FASTA base sequence file
 * providing utilities for common sequence
 * file tasks.
 */
public class FastaBaseSequenceFile {
    
    /**
     * Private FASTA base sequence file member
     */
    private File fastaBaseSequenceFile;
    
    /**
     * Private FASTA base sequence file reader member
     */
    private BufferedReader fastaBaseSequenceFileReader;
    
    /**
     * Private FASTA base sequence file header member
     */
    private String fastaBaseSequenceFileHeader;
    
    /**
     * Private sequence compliment flag member
     */
    private boolean baseSequenceCompliment;
        
    /**
     * Constructs a new FASTA base sequence file
     * 
     * @param filename Fully qualified FASTA sequence filename
     */
    public FastaBaseSequenceFile( String filename ) throws Exception {
        this.fastaBaseSequenceFile = new File( filename );
        if ( !this.fastaBaseSequenceFile.canRead() ) {
            throw new Exception( "Cannot access FASTA sequence file specified." );   
        }
    }
    
    /**
     * Intializes the FASTA sequence file resetting
     * the file pointer to the first base in
     * sequence (e.g. phase of 0).
     */
    public void initialize() throws Exception {
        this.initialize(0);
    }
    
    /**
     * Initializes the FASTA sequence file resetting
     * the file pointer to the specified base as
     * determined by the phase provided.
     */
    public void initialize( int phase ) throws Exception {
        
        try {
            
            // open up a file reader and assign to class member
            this.fastaBaseSequenceFileReader = new BufferedReader( 
                new FileReader(this.fastaBaseSequenceFile) );
                
            // extract the FASTA sequence file header
            // (e.g. the line that begins with '>'
            this.fastaBaseSequenceFileHeader = 
                this.fastaBaseSequenceFileReader.readLine();
            
            // move to the starting nucleotide based on the phase
            while( phase != 0 ) {
                this.fastaBaseSequenceFileReader.read();
                --phase;
            }
        
        } catch (IOException ioe) {
            throw new Exception( "Error accessing FASTA sequence file." );
        }
        
        // default the sequence compliment flag
        this.baseSequenceCompliment = false;
    }
    
    /**
     * Compliments the FASTA base sequence.
     * Note that complimenting a sequence that
     * has already been complimented will revert
     * the sequence back to its original state.
     */
    public void complimentSequence() throws Exception {
        
        // check to make sure the file has been
        // initialized.  if not then throw an exception
        if ( this.fastaBaseSequenceFileReader == null ) {
            throw new Exception( "FASTA sequence file not initiated. Unable to compliment" );   
        }
        this.baseSequenceCompliment = !this.baseSequenceCompliment;
        
    }
    
    /**
     * Reads the next codon from the FASTA sequence file.
     * Note that a null is returned if the end of the file
     * is reached.
     *
     * @return Three letter base codon sequence
     */
    public String readCodon() throws Exception {

        // check to make sure the file has been
        // initialized.  if not then throw an exception
        if ( this.fastaBaseSequenceFileReader == null ) {
            throw new Exception( "FASTA sequence file not initiated." );   
        }
        
        StringBuffer bases = new StringBuffer();
        int fastaBaseSequenceFileCharacter;
        int baseCount = 0;
        
        // read the next three characters from the file
        // if the character is "new line", then move to
        // the next base in the sequence.  if the end
        // of the sequence is reached, return NULL
        while( baseCount < 3 ) {
            
            // read the next character from the file
            fastaBaseSequenceFileCharacter = 
                this.fastaBaseSequenceFileReader.read();
            
            // check if the end of file was reached
            if ( fastaBaseSequenceFileCharacter == -1 ) {
                return null;
                
            } else if ( (char)fastaBaseSequenceFileCharacter != '\n' ) {
                
                // if the sequence compliment flag is TRUE
                // commpliment then add the base
                if ( this.baseSequenceCompliment ) {
                    bases.append( this.getComplimentBase((char)fastaBaseSequenceFileCharacter) );
                } else {
                    bases.append( (char)fastaBaseSequenceFileCharacter );
                }
                
                // udpate the number of bases processed 
                ++baseCount;
            }
        }

        return bases.toString();
        
    }
    
    /**
     * Gets the FASTA base sequence file header.
     *
     * @return FASTA base sequence file header
     */
    public String getFileHeader() throws Exception {
        
        // check to make sure the file has been
        // initialized.  if not then throw an exception
        if ( this.fastaBaseSequenceFileReader == null ) {
            throw new Exception( "FASTA sequence file not initiated." );   
        }
        return this.fastaBaseSequenceFileHeader;
    
    }
    
    /**
     * Generates the compliment base
     *
     * @param base Base character
     * @return Compliment base character
     */
    private char getComplimentBase( char base ) throws Exception {
        if ( (base == 'G') || (base == 'g') )
            return 'C';
        else if ( (base == 'C') || (base == 'c') )
            return 'G';
        else if ( (base == 'A') || (base == 'a') )
            return 'T';
        else if ( (base == 'T') || (base == 't') )
            return 'A';
        else
            // unknown base encountered, throw exception
            throw new Exception( "Unknown base encountered.  Cannot generate compliment." );
    }
        
}