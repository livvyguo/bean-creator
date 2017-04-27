package org.lvy.bc;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Poison on 24/03/2017.
 */
@Getter
@Setter
class Config {

    private String url;

    private String username;

    private String password;

    private List<String> tableList;

    private String packageName;

    private boolean useComment;

    private boolean useLocalDate;

    private boolean useLocalDateTime;

    private boolean useLombok;

}
