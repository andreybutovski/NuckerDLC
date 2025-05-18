package im.nucker.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventUpdate {
    public MatrixStack getMatrixStack() {
        return null;
    }
}
