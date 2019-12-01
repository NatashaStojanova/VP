package mk.ukim.finki.natashastojanova.vp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * @author Natasha Stojanova
 */

@Entity
@Setter
@Getter
@AllArgsConstructor
public class Pizza {
    private String name;
    private String description;
    @OneToMany(mappedBy = "pizza")
    private List<Ingredient> ingredientList;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pizaaId;



}
