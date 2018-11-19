package br.edu.ulbra.election.voter.service;

import br.edu.ulbra.election.voter.client.VoteClientService;
import br.edu.ulbra.election.voter.config.MD5;
import br.edu.ulbra.election.voter.exception.GenericOutputException;
import br.edu.ulbra.election.voter.input.v1.VoterInput;
import br.edu.ulbra.election.voter.model.Voter;
import br.edu.ulbra.election.voter.output.v1.GenericOutput;
import br.edu.ulbra.election.voter.output.v1.VoterOutput;
import br.edu.ulbra.election.voter.repository.VoterRepository;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class VoterService {

	private final VoterRepository voterRepository;
	private final VoteClientService voteClientService;
	private final ModelMapper modelMapper;

	private static final String MESSAGE_INVALID_ID = "Invalid id";
	private static final String MESSAGE_VOTER_NOT_FOUND = "Voter not found";

	@Autowired
	public VoterService(VoterRepository voterRepository, ModelMapper modelMapper, VoteClientService voteClientService) {
		this.voterRepository = voterRepository;
		this.modelMapper = modelMapper;
		this.voteClientService = voteClientService;
	}

	public List<VoterOutput> getAll() {
		Type voterOutputListType = new TypeToken<List<VoterOutput>>() {
		}.getType();
		return modelMapper.map(voterRepository.findAll(), voterOutputListType);
	}

	public VoterOutput create(VoterInput voterInput) {
		validateInput(voterInput, false, voterRepository);
		Voter voter = modelMapper.map(voterInput, Voter.class);
		voter.setPassword(MD5.md5(voter.getPassword()));
		voter = voterRepository.save(voter);
		return modelMapper.map(voter, VoterOutput.class);
	}

	public VoterOutput getById(Long voterId) {
		if (voterId == null) {
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}

		Voter voter = voterRepository.findById(voterId).orElse(null);
		if (voter == null) {
			throw new GenericOutputException(MESSAGE_VOTER_NOT_FOUND);
		}

		return modelMapper.map(voter, VoterOutput.class);
	}

	public VoterOutput update(Long voterId, VoterInput voterInput) {
		if (voterId == null) {
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}
		validateInput(voterInput, true, voterRepository);

		Voter voter = voterRepository.findById(voterId).orElse(null);
		if (voter == null) {
			throw new GenericOutputException(MESSAGE_VOTER_NOT_FOUND);
		}

		voter.setEmail(voterInput.getEmail());
		voter.setName(voterInput.getName());
		if (!StringUtils.isBlank(voterInput.getPassword())) {
			voter.setPassword(MD5.md5(voter.getPassword()));
		}
		voter = voterRepository.save(voter);
		return modelMapper.map(voter, VoterOutput.class);
	}

	public GenericOutput delete(Long voterId) {
		if (voterId == null) {
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}

		verificaVoto(voterId);

		Voter voter = voterRepository.findById(voterId).orElse(null);
		if (voter == null) {
			throw new GenericOutputException(MESSAGE_VOTER_NOT_FOUND);
		}

		voterRepository.delete(voter);

		return new GenericOutput("Voter deleted");
	}

	private void verificaVoto(Long voterId) {

		if (voteClientService.verificaVoter(voterId) == true) {
			throw new GenericOutputException("Exists votes");
		}
	}

	private void validateInput(VoterInput voterInput, boolean isUpdate, VoterRepository voterRepository) {
		if (StringUtils.isBlank(voterInput.getEmail())) {
			throw new GenericOutputException("Invalid email");
		} else {
			if (Voter.verificaEmail(voterInput.getEmail(), voterRepository)) {
				throw new GenericOutputException("Email used");
			}
		}
		if (StringUtils.isBlank(voterInput.getName()) || voterInput.getName().trim().replace(" ", "").length() < 5) {
			throw new GenericOutputException("Invalid name");
		} else {
			String name[] = voterInput.getName().split(" ");
			if (name.length < 2) {
				throw new GenericOutputException("Invalid name");
			}
		}
		if (!StringUtils.isBlank(voterInput.getPassword())) {
			if (!voterInput.getPassword().equals(voterInput.getPasswordConfirm())) {
				throw new GenericOutputException("Passwords doesn't match");
			}
		} else {
			if (!isUpdate) {
				throw new GenericOutputException("Password doesn't match");
			}
		}
	}

}
