package ge.bestline.delivery.ws.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParcelStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String reason;
    private String code;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date createdTime;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JsonIgnore
    private Parcel parcel;

    public ParcelStatusHistory(Parcel parcel, String name, String code, String reason) {
        this.parcel = parcel;
        this.reason = reason;
        this.name = name;
        this.code = code;
    }

    @PrePersist
    protected void onCreate() {
        createdTime = new Date();
    }

}
