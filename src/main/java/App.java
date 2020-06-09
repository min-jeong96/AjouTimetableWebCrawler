import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class App {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", path.toString());

        // WebDriver Option Setting
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-popup-blocking");   // 팝업 무시

        // WebDriver 객체 생성
        ChromeDriver driver = new ChromeDriver(options);

        // 웹페이지 요청
        driver.get("https://mhaksa.ajou.ac.kr:30443/public.html#!/e020101");
        try {
            Thread.sleep(1500); // WebElement 값 읽기 전 충분한 대기 시간.
            driver.manage().window().setSize(new Dimension(1000, 500));
            driver.manage().window().setPosition(new Point(500, 0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 학년도
        WebElement year = driver.findElementByXPath("//*[@ng-model='ph.search.strYy']");
        year.sendKeys("2020");

        // 학기
        WebElement term = driver.findElementByXPath("//*[@select-list='ph.COMBO_DATA_LIST.DS_SHTM_CD_SH']");
        term.sendKeys("1학기");

        // 교과구분
        WebElement subject_type = driver.findElementByXPath("//*[@select-list='ph.COMBO_DATA_LIST.DS_SUBMATT_CD_SH']");
        subject_type.sendKeys("전공과목");

        try {
            Thread.sleep(500); // WebElement 값 읽기 전 충분한 대기 시간.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 전공과목 중 전체 선택
        WebElement major = driver.findElementByXPath("//*[@select-list='ph.OPTIONAL_COMBO_DATA_LIST.DS_MJ_CD_SH2']");
        major.sendKeys("전체");

        // 검색 버튼 element click
        WebElement btn_search = driver.findElementByXPath("//*[@class='nb-buttons right btn1']");
        btn_search.click();

        try {
            Thread.sleep(2750); // WebElement 값 읽기 전 충분한 대기 시간.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<String> filtering = new ArrayList<String>();
        filtering.add("영어");
        filtering.add("공동");
        filtering.add("야간");
        filtering.add("CYBER강좌반");

        // 수업시간표 정보 담을 ArrayList
        List<List> data = new ArrayList<List>();
        while (true) {
            try {
                Thread.sleep(250); // WebElement 값 읽기 전 충분한 대기 시간.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String[] tmp_data = driver.findElementByXPath("//*[@class='sp-grid-body']").getText().toString().split("\n");
            List<String> tmp = new ArrayList<String>(Arrays.asList(tmp_data));
            tmp.removeAll(filtering);

            while (!tmp.isEmpty()) {
                int index = 0;
                String[] tmp_list = new String[12]; // 개설학부부터 강의실 정보까지
                Arrays.fill(tmp_list, "null");
                while (true) {
                    index++;
                    if (tmp.get(index).length() == 4 && Character.isAlphabetic(tmp.get(index).charAt(0)) &&
                        tmp.get(index).substring(1, 4).chars().allMatch(Character::isDigit)) { // 수업 코드
                        tmp_list[4] = tmp.get(index++);
                        break;
                    }
                }
                for (int i = index-2; i >= 0; i--) {
                    if (!(tmp.get(i).contains("학부") || tmp.get(i).contains("학과") || tmp.get(i).contains("전공"))) {
                        if (tmp.get(i).split(" ")[0].chars().allMatch(c -> (Character.isLowerCase(c) || Character.isUpperCase(c)))) {
                            tmp_list[3] = tmp.get(i); // 강의 영문명
                            tmp_list[2] = tmp.get(i-1); // 강의 한글명
                            i--;
                        }
                        else {
                            tmp_list[2] = tmp.get(i);
                        }
                    } else if (tmp.get(i).contains("(과)")) {
                        tmp_list[1] = tmp.get(i); // 개설전공
                    } else {
                        tmp_list[0] = tmp.get(i); // 개설학부
                    }
                }
                tmp_list[5] = tmp.get(index++); // 교과구분
                while (index < tmp.size()  && index < 12 // IndexOutOfBoundException Handling
                        && !(tmp.get(index).contains("학부") || tmp.get(index).contains("학과") || tmp.get(index).contains("전공"))) {
                    // 하나의 행 끝까지 데이터 추출

                    if (tmp.get(index).length() <= 1 && (tmp.get(index).charAt(0) == 'N' || tmp.get(index).charAt(0) == 'Y')) {
                        tmp_list[6] = tmp.get(index); // 공학인증여부
                    } else if (tmp.get(index).length() <= 1 && Character.isDigit(tmp.get(index).charAt(0))) {
                        tmp_list[7] = tmp.get(index); // 학점
                        if (tmp.get(index+1).length() <= 1 && Character.isDigit(tmp.get(index+1).charAt(0))) {
                            tmp_list[8] = tmp.get(++index); // 시간
                        }
                    } else if (!(tmp.get(index).split(" ")[0].contains("(")|| tmp.get(index).split(" ")[0].contains("~"))
                            && !Character.isDigit(tmp.get(index).charAt(tmp.get(index).length()-1))) {
                        tmp_list[9] = tmp.get(index); // 교수명
                    } else if (tmp.get(index).split(" ")[0].contains("(") || tmp.get(index).split(" ")[0].contains(")")
                            || tmp.get(index).split(" ")[0].contains("~")) {
                        tmp_list[10] = tmp.get(index); // 강의시간
                    } else {
                        tmp_list[11] = tmp.get(index); // 강의실
                    }
                    index++;
                }

                for (int i = 0; i < index; i++) tmp.remove(0);
                System.out.println(Arrays.asList(tmp_list));
                data.add(Arrays.asList(tmp_list));
            }

            // 테이블 탐색 종료 조건
            WebElement state = driver.findElementByXPath("//*[@class='nb-paging-info ng-binding']");
            List<String> is_last = Arrays.asList(state.getText().toString().split(" "));
            if (is_last.get(0).equals(is_last.get(2))) break;

            // 다음 버튼 click
            WebElement btn_next = driver.findElementByXPath("//*[@ng-click='$paging.nextPage()']");
            btn_next.click();
        }

        for (List datum : data) System.out.println(datum);

        // 웹페이지 소스 출력
        // System.out.println(driver.getPageSource());

        // 종료
        /*
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }*/
    }
}
