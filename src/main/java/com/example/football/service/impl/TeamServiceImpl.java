package com.example.football.service.impl;

import com.example.football.models.dto.TeamDto;
import com.example.football.models.entity.Team;
import com.example.football.repository.TeamRepository;
import com.example.football.service.TeamService;
import com.example.football.service.TownService;
import com.example.football.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class TeamServiceImpl implements TeamService {

    private static final String TEAM_FILE_PATH = "src/main/resources/files/json/teams.json";
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final TownService townService;

    public TeamServiceImpl(TeamRepository teamRepository, ModelMapper modelMapper, Gson gson, ValidationUtil validationUtil, TownService townService) {
        this.teamRepository = teamRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.townService = townService;
    }


    @Override
    public boolean areImported() {
        return teamRepository.count() > 0;
    }

    @Override
    public String readTeamsFileContent() throws IOException {
        return Files.readString(Path.of(TEAM_FILE_PATH));
    }

    @Override
    public String importTeams() throws IOException {
        StringBuilder sb = new StringBuilder();


//        TeamDto[] teamDtos = gson.fromJson(readTeamsFileContent(), TeamDto[].class);
//        List<Team> collect = Arrays.stream(teamDtos).filter(teamDto -> {
//            return validationUtil.isValid(teamDto)
//                    && !isExist(teamDto.getName());
//        }).map(teamDto -> {
//            Team team = modelMapper.map(teamDto, Team.class);
//            team.setTown(townService.findByName(team.getTown().getName()));
//            return team;
//        }).collect(Collectors.toList());
//        System.out.println();
//
        Arrays.stream(gson.fromJson(readTeamsFileContent(), TeamDto[].class))
                .filter(teamDto -> {
                    boolean isValid = validationUtil.isValid(teamDto)
                            && !isExist(teamDto.getName());


                    sb
                            .append(isValid ? String.format("Successfully imported Team %s - %d",
                                    teamDto.getName(), teamDto.getFanBase())
                                    : "Invalid Team")
                            .append(System.lineSeparator());


                    return isValid;
                })
                .map(teamDto -> {
                    Team team = modelMapper.map(teamDto,Team.class);
                    team.setTown(townService.findByName(team.getTown().getName()));
                    return team;
                })
                .forEach(teamRepository::save);


        return sb.toString();
    }

    public boolean isExist(String name) {
        return teamRepository.findByName(name) != null;
    }

    @Override
    public Team getById(Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    @Override
    public Team getByName(String name) {
        return teamRepository.findByName(name);
    }
}
