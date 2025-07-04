package com.gym.crm.app.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "trainings")
@Getter
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "training_name", nullable = false, length = 32)
    private String trainingName;

    @Column(name = "training_date")
    private LocalDate trainingDate;

    @Column(name = "training_duration")
    private BigDecimal trainingDuration;

    @ManyToOne
    @JoinColumn(name = "trainer_id", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "fk_training_trainer"))
    private Trainer trainer;

    @ManyToOne
    @JoinColumn(name = "trainee_id", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "fk_training_trainee"))
    private Trainee trainee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "training_type_id", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "fk_training_training_type"))
    private TrainingType trainingType;
}