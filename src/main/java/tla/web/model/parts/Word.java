package tla.web.model.parts;
    
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Word {

    private Transcription transcription;

    private Glyphs glyphs;

}
