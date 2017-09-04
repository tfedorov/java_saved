/**
 * 
 */
package ua.com.make_bet.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ua.com.make_bet.bean.Matche;

@Service
public class MatchService {

  public List<Matche> getAllMatches() {
    List<Matche> result = new ArrayList<Matche>();
    result.add(new Matche("A", "France", "Senegal"));
    result.add(new Matche("A", "France", "Peru"));
    return result;
  }
}
