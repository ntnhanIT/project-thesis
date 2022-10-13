package com.edu.ctu.thesis.seafood.TraiNuoi;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.edu.ctu.thesis.audit.Audit;
import com.edu.ctu.thesis.audit.AuditInterface;
import com.edu.ctu.thesis.audit.AuditListener;
import com.edu.ctu.thesis.seafood.user.User;
import com.edu.ctu.thesis.seafood.vungnuoi.VungNuoi;
import com.edu.ctu.thesis.validity.Validity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "trai_nuoi")
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@EntityListeners(AuditListener.class)
@EqualsAndHashCode(callSuper = false, of = { "user" })
@Slf4j
public class TraiNuoi extends Validity implements AuditInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_trai_nuoi", nullable = false)
    @NotBlank(message = "Ten trai nuoi khong duoc phep bo trong")
    private String tenTraiNuoi;

    @Column(name = "dia_chi", nullable = false)
    @NotBlank(message = "Dia chi trai nuoi khong duoc phep bo trong")
    private String diaChi;

    @Column(name = "dien_thoai")
    private String dienThoai;

    @Column(name = "hinh_thuc_nuoi")
    private String hinhThucNuoi;

    @Column(name = "doi_tuoi_nuoi")
    private String doiTuoiNuoi; // TODO: change type --> doi tuong nuoi tom

    @Column(name = "dien_tich_nuoi")
    private Float dienTichNuoi;

    @OneToOne(mappedBy = "traiNuoi", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @NotNull(message = "User should not be null")
    private User user;

    @OneToMany(mappedBy = "traiNuoi", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<VungNuoi> vungNuois;

    // @JsonIgnore
    @JsonProperty(access = Access.READ_ONLY)
    @Embedded
    private Audit audit;

    public void copy(TraiNuoi traiNuoi) {
        this.tenTraiNuoi = traiNuoi.tenTraiNuoi;
        this.diaChi = traiNuoi.diaChi;
        this.dienThoai = traiNuoi.dienThoai;
        this.hinhThucNuoi = traiNuoi.hinhThucNuoi;
        this.doiTuoiNuoi = traiNuoi.doiTuoiNuoi;
        this.dienTichNuoi = traiNuoi.dienTichNuoi;

        this.copyUserFullName(traiNuoi);
        this.copyVungNuois(traiNuoi.vungNuois);
    }

    private void copyUserFullName(TraiNuoi traiNuoi) {
        String userFullName = traiNuoi.user.getFullName();
        if (StringUtils.isNotBlank(userFullName)) {
            this.user.setFullName(userFullName);
        }
    }

    private void copyVungNuois(List<VungNuoi> vungNuois) {
        if (!CollectionUtils.isEmpty(this.vungNuois) && !CollectionUtils.isEmpty(vungNuois)) {
            for (VungNuoi vungNuoi : vungNuois) {
                VungNuoi vungNuoiInDB = this.vungNuois.stream().filter(e -> e.getId().equals(vungNuoi.getId()))
                        .findFirst().orElse(null);
                if (vungNuoiInDB != null) {
                    vungNuoiInDB.copy(vungNuoi);
                }
            }
        }
    }

    @Override
    public void setCreationUser(String creationUser) {
        if (this.audit == null) {
            this.audit = new Audit();
        }
        this.audit.setCreationUser(this.user.getUsername());
        log.info("Creation user [{}]", this.audit.getCreationUser());
    }

    @Override
    public void setCreationTime(LocalDateTime creationTime) {
        if (this.audit == null) {
            this.audit = new Audit();
        }
        this.audit.setCreationTime(creationTime);
        log.info("Creation time [{}]", this.audit.getCreationTime());
    }

    @Override
    public void setModificationUser(String modificationUser) {
        if (this.audit == null) {
            this.audit = new Audit();
        }
        this.audit.setModificationUser(this.user.getUsername());
        log.info("Modification user [{}]", this.audit.getModificationUser());
    }

    @Override
    public void setModificationTime(LocalDateTime modificationTime) {
        if (this.audit == null) {
            this.audit = new Audit();
        }
        this.audit.setModificationTime(modificationTime);
        log.info("Modification time [{}]", this.audit.getModificationTime());
    }

}
