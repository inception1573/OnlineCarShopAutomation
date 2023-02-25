package org.example;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
public class Main {
    private static WebDriver driver;
    private static int implicitWaitInSecond=5000;
    private static void setup(String url) throws InterruptedException {
        WebDriverManager.getInstance(DriverManagerType.CHROME).setup();
        driver=new ChromeDriver();
        driver.navigate().to(url);
        driver.manage().window().maximize();
        Thread.sleep(5000);
    }
    private static void waitForVisibility(By by){
        WebDriverWait wait = new WebDriverWait(driver,implicitWaitInSecond/1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    private static void waitForClickable(By by){
        WebDriverWait wait = new WebDriverWait(driver,implicitWaitInSecond/1000);
        wait.until(ExpectedConditions.elementToBeClickable(by));
    }
    public static void main(String[] args) throws InterruptedException {

        automateScenario2();

        driver.close();
    }

    private static void automateScenario1() throws InterruptedException {
        setup("https://www.roimotors.com/");
        int numberOfCivic=0;
        List<WebElement> dropdownList=driver.findElements(By.className("right-border"));

        for(int i=0;i< dropdownList.size()-1;i++)
        {
            dropdownList.get(i).click();

            List<WebElement> options= driver.findElements(By.cssSelector(".select-options>ul>li"));

            //i==0 indicates first dropdown
            //i==1 indicates second dropdown
            //i==2 indicates third dropdown
            if(i==0)
            {
                //select New
                options.get(1).click();
            } else if (i==1) {
                //select 2023
                options.get(1).click();
            } else if (i==2) {
                waitForVisibility(By.xpath("//div[@id='new-facet-browse']//div[@class='facet-list']"));
                List<WebElement> facetList= driver.findElements(By.xpath("//div[@id='new-facet-browse']//div[@class='facet-list']"));
                //select Model
                facetList.get(3).click();
                waitForVisibility(By.cssSelector(".facet-list-group>div>ol>li"));
                WebElement civicWebElement= driver.findElement(By.xpath("//span[@class='facet-list-facet-label-text' and text()='Civic']"));
                //select Civic
                civicWebElement.click();
                //getting number of civic
                WebElement sibling = civicWebElement.findElement(By.xpath("following-sibling::*"));
                numberOfCivic=Integer.parseInt(sibling.getText());
                System.out.println("Number of Civic: "+ numberOfCivic);
                //click on search button
                driver.findElement(By.cssSelector(".p-0.modal-footer")).click();

                waitForVisibility(By.id("inventory-filters1-app-root"));
                List<WebElement> spanList =  driver.findElements(By.cssSelector(".my-1>strong>span"));

                String searchResultText = spanList.get(0).getAttribute("innerHTML");
                String[] splitedSearchResult= (searchResultText.split(" ",2));
                int numberOfCivicInDescription=Integer.parseInt(splitedSearchResult[0]);

                System.out.println("Number of Civic in description: "+numberOfCivicInDescription);

                if (numberOfCivicInDescription==numberOfCivic)
                {
                    System.out.println("Passed!!");
                }
            }
        }
    }

    private static void automateScenario2() throws InterruptedException {
        setup("https://www.roimotors.com/new-inventory/index.htm");

        waitForVisibility(By.xpath("//div[@id='year']//div[@id='year--heading']"));
        WebElement elementYear= driver.findElement(By.xpath("//div[@id='year']//div[@id='year--heading']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", elementYear);
        elementYear.click();

        waitForVisibility(By.id("year-max"));
        WebElement inputField = driver.findElement(By.id("year-max"));
        //clear default value
        String value=inputField.getAttribute("value");
        if(value!=null) {
            for (int i = 0; i < value.length(); i++) {
                inputField.sendKeys(Keys.BACK_SPACE);
            }
        }

        inputField.sendKeys("2022");
        inputField.sendKeys(Keys.TAB);

        List<WebElement> pageWebElementList=driver.findElements(By.xpath("//ul[@class='pagination']//li"));
        int totalCarDisplayed=0;
        List<String> titleList=new ArrayList<>();
        for(int count=0;count<pageWebElementList.size()-2;count++){
            if(count>0) {
                JavascriptExecutor js = (JavascriptExecutor)driver;
                js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'})", pageWebElementList.get(count));
                pageWebElementList.get(count).click();
            }

            Thread.sleep(8000);

            waitForVisibility(By.xpath("//li[contains(@class,'vehicle-card vehicle-card-detailed')]//div[contains(@class,'vehicle-card-body')]"));
            List<WebElement> carWebElementList=driver.findElements(By.xpath("//li[contains(@class,'vehicle-card vehicle-card-detailed')]//div[contains(@class,'vehicle-card-body')]"));
            totalCarDisplayed+=carWebElementList.size();
            for(int j=0;j<carWebElementList.size();j++) {
                    try {
                        String title = carWebElementList.get(j).findElement(By.xpath("//div[@class='vehicle-card-details-container']/h2/a/span")).getAttribute("innerHTML");
                        titleList.add(title);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
            }
        }
        List<WebElement> spanList =  driver.findElements(By.cssSelector(".my-1>strong>span"));
        String searchResultText = spanList.get(0).getAttribute("innerHTML");
        String[] splitedSearchResult= (searchResultText.split(" ",2));
        int numberOfCarFound=Integer.parseInt(splitedSearchResult[0]);

        //System.out.println("car displayed "+totalCarDisplayed);
        System.out.println("1. Number of vehicles in the \"38 Vehicles Matching\" is the same as the actual number of vehicles displayed.");
        if(totalCarDisplayed==numberOfCarFound)
        {
            System.out.println("Status: Passed!");
        }
        else{
            System.out.println("Status: Failed!");
        }

        System.out.println("2. Verify all 38 vehicles listed on the search page has the title \"2022 Honda\"");

        //System.out.println("Title array size: "+titleList.size());
        boolean result=true;
        for (int index=0;index< titleList.size();index++)
        {
            //System.out.println("---------------------------------------------------------------");
            String title=titleList.get(index).trim();
            //System.out.println(title);

            if(!title.equals("2022 Honda")){
                System.out.println(titleList.get(index).trim());
                result=false;
                break;
            }
        }

        if(numberOfCarFound==titleList.size()&&result==true)
        {
            System.out.println("Status: Passed!");
        }
        else {
            System.out.println("Status: Failed!");
        }
    }
}