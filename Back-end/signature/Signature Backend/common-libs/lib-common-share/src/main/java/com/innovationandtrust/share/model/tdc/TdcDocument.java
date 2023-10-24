package com.innovationandtrust.share.model.tdc;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TdcDocument implements Serializable {
    private UUID uuid;
    private UUID baseUUID;
    private String baseID;
    private UUID fileID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TdcDocument tdcDocument)) return false;
        return Objects.equals(getUuid(), tdcDocument.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }
}
