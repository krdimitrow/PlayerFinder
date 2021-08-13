package com.example.football.service.impl;

import com.example.football.models.dto.PlayerDto;
import com.example.football.models.dto.PlayerRootDto;
import com.example.football.models.entity.Player;
import com.example.football.repository.PlayerRepository;
import com.example.football.service.PlayerService;
import com.example.football.service.StatService;
import com.example.football.service.TeamService;
import com.example.football.service.TownService;
import com.example.football.util.ValidationUtil;
import com.example.football.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {

    private static final String PLAYER_FILE_PATH = "src/main/resources/files/xml/players.xml";

    private final PlayerRepository playerRepository;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final ValidationUtil validationUtil;
    private final StatService statService;
    private final TeamService teamService;
    private final TownService townService;

    public PlayerServiceImpl(PlayerRepository playerRepository, ModelMapper modelMapper, XmlParser xmlParser, ValidationUtil validationUtil, StatService statService, TeamService teamService, TownService townService) {
        this.playerRepository = playerRepository;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
        this.statService = statService;
        this.teamService = teamService;
        this.townService = townService;
    }


    @Override
    public boolean areImported() {
        return playerRepository.count()>0;
    }

    @Override
    public String readPlayersFileContent() throws IOException {
        return Files.readString(Path.of(PLAYER_FILE_PATH));
    }

    @Override
    public String importPlayers() throws JAXBException, FileNotFoundException {
        StringBuilder sb = new StringBuilder();

        xmlParser.fromFile(PLAYER_FILE_PATH, PlayerRootDto.class).getPlayers().
        stream().filter(playerDto -> {
                    boolean isValid = validationUtil.isValid(playerDto)
                            && !isExist(playerDto.getEmail())
                            && isStatExists(playerDto.getStat().getId());

                    sb
                            .append(isValid ? String.format("Successfully imported Player %s %s - %s",
                                    playerDto.getFirstName(),playerDto.getLastName(),playerDto.getPosition().toString()):
                                    "Invalid Player")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(playerDto -> {
                    Player player = modelMapper.map(playerDto, Player.class);
                    player.setStat(statService.findById(playerDto.getStat().getId()));
                    player.setTeam(teamService.getByName(playerDto.getTeam().getName()));
                    player.setTown(townService.findByName(playerDto.getTown().getName()));
                    return player;
                })
                .forEach(playerRepository::save);





        return sb.toString();
    }

    private boolean isStatExists(Long id) {
        return statService.findById(id)!=null;
    }

    public boolean isExist(String email) {
       return playerRepository.findByEmail(email) != null;
    }

    @Override
    public String exportBestPlayers() {
        StringBuilder sb = new StringBuilder();

        playerRepository.findTheBestPlayerOrderByShootingThenPassingThenEnduranceDescThenByLastName()
                .forEach(player -> {

                    sb
                            .append(String.format("Player - %s %s\n" +
                                    "\tPosition - %s\n" +
                                    "Team - %s\n" +
                                    "\tStadium - %s\n",
                                    player.getFirstName(),player.getLastName(),
                                    player.getPosition().toString(),
                                    player.getTeam().getName(),
                                    player.getTeam().getStadiumName())).append(System.lineSeparator());



                });





        return sb.toString();
    }
}
