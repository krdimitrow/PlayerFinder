package com.example.football.service;


import com.example.football.models.entity.Team;

import java.io.IOException;

public interface TeamService {
    boolean areImported();

    String readTeamsFileContent() throws IOException;

    String importTeams() throws IOException;

    boolean  isExist(String name);

    Team getById(Long id);

    Team getByName(String name);

}
