package allday.minico.service.oxgame;

import allday.minico.dao.oxgame.OxGameSettingDAO;
import allday.minico.dto.oxgame.ProblemTypeDTO;

import java.util.List;

public class OxGameSettingService {
    private static final OxGameSettingDAO settingDAO = OxGameSettingDAO.getInstance();
    private static OxGameSettingService instance;

    public static OxGameSettingService getInstance() {
        if(instance == null) {
            instance = new OxGameSettingService();
        }
        return instance;
    }

    public List<ProblemTypeDTO> getProblemType() {
        try{
            return settingDAO.findAllProblemType();
        } catch (Exception e) {
            System.out.println("주제를 불러오는데 실패하였습니다. " + e.getMessage());
        }
        return null;
    }


}
