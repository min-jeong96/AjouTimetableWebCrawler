import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {
        Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", path.toString());

        // WebDriver Option Setting
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");          // 전체 화면으로 실행
        options.addArguments("--disable-popup-blocking");   // 팝업 무시
        options.addArguments("--start-maximized");          // 기본 앱 사용 안 함

        // WebDriver 객체 생성
        ChromeDriver driver = new ChromeDriver(options);

        // 웹페이지 요청
        driver.get("https://mhaksa.ajou.ac.kr:30443/public.html#!/e020101");
        try {
            Thread.sleep(1500); // WebElement 값 읽기 전 충분한 대기 시간.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement year = driver.findElementByXPath("//*[@ng-model='ph.search.strYy']");
        year.sendKeys("2020");
        List<String> years = Arrays.asList(year.getText().toString().split("\n"));
        System.out.println(years);

        WebElement term = driver.findElementByXPath("//*[@select-list='ph.COMBO_DATA_LIST.DS_SHTM_CD_SH']");
        term.sendKeys("1학기");
        List<String> terms = Arrays.asList(term.getText().toString().split("\n"));
        System.out.println(terms);

        WebElement subject_type = driver.findElementByXPath("//*[@select-list='ph.COMBO_DATA_LIST.DS_SUBMATT_CD_SH']");
        subject_type.sendKeys("전공과목");
        List<String> subject_types = Arrays.asList(subject_type.getText().toString().split("\n"));
        System.out.println(subject_types);

        try {
            Thread.sleep(1500); // WebElement 값 읽기 전 충분한 대기 시간.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 전공과목 중 전체 선택
        // ...

        // 검색 버튼 element click
        // ...

        // 웹페이지 소스 출력
        System.out.println(driver.getPageSource());

        // 종료
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }
    }
}
