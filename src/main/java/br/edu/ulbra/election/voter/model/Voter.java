package br.edu.ulbra.election.voter.model;

import javax.persistence.*;

import br.edu.ulbra.election.voter.repository.VoterRepository;

@Entity
public class Voter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public static boolean verificaEmail(String email, VoterRepository voterRepository) {

		Iterable<Voter> list = voterRepository.findAll();

		for (Voter e : list) {
			if (e.getEmail().equals(email)) {
				return true;
			}
		}
		return false;

	}
}
