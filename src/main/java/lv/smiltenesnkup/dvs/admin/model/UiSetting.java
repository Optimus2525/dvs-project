package lv.smiltenesnkup.dvs.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

/**
 * Pārstāv sistēmas vizuālo iestatījumu (CSS mainīgo).
 */
@Entity
@Table(name = "ui_setting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UiSetting {

    @Id
    @Column(name = "setting_key", length = 50)
    private String settingKey;

    @Column(name = "setting_value", nullable = false)
    private String settingValue;

}