/*
 * Copyright Curtin University, 2015.
 */
package au.edu.curtin.lims.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import au.edu.curtin.lims.db.IAnalysisCapabilityRepository;
import au.edu.curtin.lims.db.IAnalysisRequestRepository;
import au.edu.curtin.lims.db.IAnalysisRequestStateRepository;
import au.edu.curtin.lims.db.IGeosampleRepository;
import au.edu.curtin.lims.db.IResultRepository;
import au.edu.curtin.lims.domain.AnalysisCapability;
import au.edu.curtin.lims.domain.AnalysisRequest;
import au.edu.curtin.lims.domain.AnalysisRequestState;
import au.edu.curtin.lims.domain.Geosample;
import au.edu.curtin.lims.domain.Result;
import au.edu.curtin.lims.form.AddOrRemoveGeosampleToAnalysisRequest;
import au.edu.curtin.lims.form.AnalysisRequestForm;
import au.edu.curtin.lims.viewmodel.AnalysisRequestDetailViewModel;
import au.edu.curtin.lims.viewmodel.ResultViewModel;

import org.hibernate.criterion.Restrictions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/AnalysisRequest")
public class AnalysisRequestController extends BaseController {
    @Inject
    private IAnalysisRequestRepository analysisRequestRepository;
    
    @Inject
    private IAnalysisRequestStateRepository analysisRequestStateRepository;
    
    @Inject
    private IAnalysisCapabilityRepository analysisCapabilityRepository;
    
    @Inject
    private IGeosampleRepository geosampleRepository;
    
    @Inject
    private IResultRepository resultResository;
    
    private final String indexTemplate = "<!DOCTYPE html>\n" + 
            "<html lang=\"en\">\n" + 
            "    <head>\n" + 
            "        <meta charset=\"utf-8\" />\n" + 
            "        <title>IGSN:%1$s - Mineralogical Classification Data</title>\n" + 
            "    </head>\n" + 
            "    <body>\n" + 
            "        <div>\n" + 
            "            <h1>Mineralogical Classification Data for IGSN:%1$s</h1>\n" + 
            "            <p>\n" + 
            "                The following datasets relate to geo sample IGSN:%1$s. Metadata for this sample can be found <a href=\"http://hdl.handle.net/10273/%1$s\">here</a>.\n" + 
            "            </p>\n" + 
            "            <h3>Description of files:</h3>\n" + 
            "            <p>\n" + 
            "                <a href=\"/gswa-library/%2$d/%1$s/%1$s.project.zip\">%1$s.project.zip</a>:<br/>\n" + 
            "                Raw export of TESCAN TIMA scan data for sample %1$s.<br/>\n" + 
            "                The contents of this file were created by TESCAN TIMA3 Control Software v1.4.0.8. Please refer to <a href=\"http://www.tescan.com\">TESCAN</a> for more information.\n" + 
            "            </p>\n" + 
            "            <p>\n" + 
            "                <a href=\"/gswa-library/%2$d/%1$s/%1$s.mindif.zip\">%1$s.mindif.zip</a>:<br/>\n" + 
            "                MinDIF export of TESCAN TIMA scan data for sample %1$s.<br/>\n" + 
            "                The contents of this file were created by TESCAN TIMA3 Control Software v1.4.0.8. Please refer to <a href=\"http://www.tescan.com\">TESCAN</a> for more information.\n" + 
            "            </p>\n" + 
            "            <p>\n" + 
            "                <a href=\"/gswa-library/%2$d/%1$s/%1$s.classification.png\">%1$s.classification.png</a>:<br/>\n" + 
            "                The mineralogical classification of sample %1$s. Derived from %1$s.mindif.zip.<br/>\n" + 
            "                <img src=\"/gswa-library/%2$d/%1$s/%1$s.classification.thumbnail.png\" alt=\"Mineralogical classfication of %1$s\" height=\"300\" width=\"300\" />\n" + 
            "            </p>\n" + 
            "            <p>\n" + 
            "                <a href=\"/gswa-library/%2$d\">Back to parent folder</a>\n" + 
            "            </p>\n" + 
            "        </div>\n" + 
            "    </body>\n" + 
            "</html>";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisRequestController.class);
    
    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    @RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
    public ModelAndView index(
            Locale locale) {
        ModelAndView modelAndView = this.getModelAndView("index", locale);
        List<AnalysisRequest> analysisRequests = analysisRequestRepository.findAll();
        modelAndView.addObject("analysisRequests", AnalysisRequestDetailViewModel.createList(analysisRequests));
        return modelAndView;
    }

    @RequestMapping(value = "/{analysisRequestId:[0-9]+}", method = RequestMethod.GET)
    public ModelAndView detail(
            @PathVariable("analysisRequestId") final int analysisRequestId,
            Locale locale) throws Exception {
        AnalysisRequest analysisRequest = analysisRequestRepository.findById(analysisRequestId);
        
        AnalysisRequestDetailViewModel analysisRequestVM = 
                new AnalysisRequestDetailViewModel(analysisRequest);
        
        String requestStateName = analysisRequest.getAnalysisRequestState().getAnalysisRequestStateName();
        
        ModelAndView modelAndView;
        
        switch (requestStateName) {
        case "Under Construction":
            modelAndView = this.getModelAndView("detail.under_construction", locale);
            
            // TODO: Here I'm adding all the geosamples to the model to allow the user the add them to this AR. It would be nice to restrict this list to only geosamples that had an appropriate mount_type_id for this type of analysis...
            modelAndView.addObject("geosamples", this.geosampleRepository.findAll());
            break;
        case "Specified":
            modelAndView = this.getModelAndView("detail.specified", locale);

            List<Result> results = analysisRequest.getResults();
            int[] geosampleIdsThatHaveResults = new int[results.size()];

            // TODO: Refactor this part.
            int i = 0;
            for (Result result : results) {
                geosampleIdsThatHaveResults[i] = result.getGeosample().getGeosampleId();
                i++;
            }
            
            modelAndView.addObject("geosampleIdsThatHaveResults", geosampleIdsThatHaveResults);
            modelAndView.addObject("geosamples", analysisRequest.getGeosamples().stream().sorted().collect(Collectors.toList()));
            break;
        case "Complete":
            modelAndView = this.getModelAndView("detail.complete", locale);
            modelAndView.addObject("results", ResultViewModel.createList(
                    analysisRequest.getResults()));
            break;
        default:
            // TODO: Make specific exception.
            throw new Exception(String.format("Unrecognised request state with name: {0}.", requestStateName));
        }
       
        modelAndView.addObject("analysisRequest", analysisRequestVM);
        return modelAndView;
    }

    @RequestMapping(value = "/Create", method = RequestMethod.GET)
    public ModelAndView create(Locale locale) {
        ModelAndView modelAndView = this.getModelAndView("create", locale);
        List<AnalysisCapability> analysisCapabilities = this.analysisCapabilityRepository.findAll();
        
        modelAndView.addObject("analysisRequestForm", new AnalysisRequestForm());
        modelAndView.addObject("analysisCapabilities", analysisCapabilities);
        return modelAndView;
    }

    @RequestMapping(value = "/Create", method = RequestMethod.POST)
    public String create(
            AnalysisRequestForm analysisRequestForm,
            Model model) throws Exception {
        AnalysisRequest analysisRequest = new AnalysisRequest();
        
        analysisRequest.setAnalysisCapability(
                this.analysisCapabilityRepository.findById(
                        analysisRequestForm.getAnalysisCapabilityId()));
        
        analysisRequest.setPerson(this.getPerson());
        
        // TODO: What's the best way to store these names? I.e. enum or something?
        // You could make the ORM script add this to the db types: public final static String UnderConstruction = "Under Construction";
        analysisRequest.setAnalysisRequestState(
                this.analysisRequestStateRepository.findUniqueByCriteria(
                        Restrictions.eq("analysisRequestStateName", "Under Construction")));

        analysisRequest.setRequestDate(Calendar.getInstance().getTime());
        
        this.analysisRequestRepository.save(analysisRequest);
        return "redirect:/AnalysisRequest/" + analysisRequest.getAnalysisRequestId();
    }
    
    @RequestMapping(
            value = "/AddGeosample", 
            method = RequestMethod.POST)
            //produces = MediaType.APPLICATION_JSON_VALUE,
            //consumes = MediaType.APPLICATION_JSON_VALUE
    @ResponseBody
    // TODO: change this to use a proper JSON type.
    public String addGeosample(
            AddOrRemoveGeosampleToAnalysisRequest addOrRemoveGeosampleToAnalysisRequest,
            Locale locale) {
        int analysisRequestId = addOrRemoveGeosampleToAnalysisRequest.getAnalysisRequestId();
        int geosampleId = addOrRemoveGeosampleToAnalysisRequest.getGeosampleId();
        boolean add = addOrRemoveGeosampleToAnalysisRequest.getAdd();
        
        AnalysisRequest analysisRequest = analysisRequestRepository.findById(analysisRequestId);
        Geosample geosample = geosampleRepository.findById(geosampleId);
        
        if (add) {
            analysisRequest.getGeosamples().add(geosample);  
        }
        else {
            analysisRequest.getGeosamples().remove(geosample);
        }

        this.analysisRequestRepository.save(analysisRequest);
        return "OK";                
    }
    
    @RequestMapping(value = "/Specify", method = RequestMethod.POST)
    public String specify(final int analysisRequestId) throws Exception {
        AnalysisRequest analysisRequest = this.analysisRequestRepository.findById(analysisRequestId);
        
        AnalysisRequestState analysisRequestState = this.analysisRequestStateRepository.findUniqueByCriteria(
                Restrictions.eq("analysisRequestStateName", "Specified"));
        
        analysisRequest.setAnalysisRequestState(analysisRequestState);
        this.analysisRequestRepository.save(analysisRequest);
        
        return "redirect:/AnalysisRequest/" + analysisRequest.getAnalysisRequestId();
    }
    
    @RequestMapping(value = "/Complete", method = RequestMethod.POST)
    public String complete(final int analysisRequestId) throws Exception {
        AnalysisRequest analysisRequest = this.analysisRequestRepository.findById(analysisRequestId);

        AnalysisRequestState analysisRequestState = this.analysisRequestStateRepository.findUniqueByCriteria(
                Restrictions.eq("analysisRequestStateName", "Complete"));
        
        analysisRequest.setAnalysisRequestState(analysisRequestState);
        this.analysisRequestRepository.save(analysisRequest);
        
        // TODO: This 'magic number' logic needs to be refactored:
        if (analysisRequest.getAnalysisCapability().getAnalysisCapabilityId() == 1) { // TODO: TIMA Specific
            // This is the point where we process the uploaded files:
            String script = AnalysisRequestController.scriptsPath + "/" + analysisRequest.getAnalysisCapability().getResultProcessorValue();
    
            StringBuilder sb = new StringBuilder("Title,Longitude,Latitude,GeospatialLocationDCMI,GeospatialLocationWKT,Description,RelatedWebsiteURL,RelatedWebsiteTitle,Keyword_2,LocationURL,ExtentOrQuality,DataSize,PublicationDate");
            for (Result result : analysisRequest.getResults()) {
                long projectFileSize = 0;
                long mindifFileSize = 0;
    
                StringBuffer output = new StringBuffer();
                Process p;
                try {
                    // These are examples of the arguments: python mindif_import.py IECUR000Q.zip IECUR000Q 4
                    String command = "python " + script + " " + AnalysisRequestController.stagingPath + "/AR" + analysisRequestId + "GS" + result.getGeosample().getGeosampleId() + " " + result.getGeosample().getIgsn() + " " + analysisRequestId;
                    LOGGER.info("Running command: {}", command);
                    p = Runtime.getRuntime().exec(command);
                    p.waitFor();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    
                    String line = "";           
                    while ((line = reader.readLine()) != null) {
                        output.append(line);
                    }
                    
                    reader.close();
                    
                    String response = output.toString();
                    String[] fragments = response.split(";");
                    
                    result.setValue(fragments[0]);
                    projectFileSize = Long.parseLong(fragments[1]);
                    mindifFileSize = Long.parseLong(fragments[2]);
                    
                    this.resultResository.save(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                Geosample geosample = result.getGeosample();
                String igsn = geosample.getIgsn();
                double longitude = geosample.getLocation().getCoordinates()[0].x;
                double latitude = geosample.getLocation().getCoordinates()[0].y;
                String rockType = geosample.getRockType();
                String siteCode = geosample.getSiteCode();
                String tectonticUnit = geosample.getTectonicUnit();
                
                // TODO: make this dynamic
                String software = "TESCAN TIMA3 Control Software v1.4.0.8";
    
                // Create the CSV: TODO: all this needs to be refactored.
                
                // ADD THE ROW FOR THE PROCESSED SEM DATA:
                sb.append(String.format("\nTIMA energy dispersive system analysis of a sample of %s IGSN:%s (Mineralogical classification)", rockType, igsn));
                sb.append("," + longitude);
                sb.append("," + latitude); 
                sb.append(",east=" + longitude + "; north=" + latitude + "; projection=WGS84");
                sb.append(",POINT(" + longitude + " " + latitude + ")");
                sb.append(String.format(",\"Mineralogical classification of %s from Geological Survey of Western Australia archive with TIMA (TESCAN Integrated Mineral Analyser) Energy-dispersive X-ray spectroscopy instrument. Sample %s from site %s, %s. File can be opened with %s.\"", 
                        rockType,
                        igsn,
                        siteCode,
                        tectonticUnit,
                        software));
    
                sb.append(String.format(",http://hdl.handle.net/10273/%s", igsn));
                sb.append(",International Geo Sample Number registry");
                sb.append(",Mineralogical classification");
                sb.append(String.format(",http://ddfe.curtin.edu.au/gswa-library/%d/%s", analysisRequest.getAnalysisRequestId(), igsn));
                sb.append(",\"2 Zip Files, 1 PNG Image.\"");
                sb.append("," + humanReadableByteCount(projectFileSize + mindifFileSize, false));
                
                sb.append(new SimpleDateFormat(",yyyy-MM-dd").format(Calendar.getInstance().getTime()));
                
                // Write the index.html file to the DDFE location:
                Writer indexWriter = null;
                try {
                    String indexFilePath = ddfePath + "/" + analysisRequest.getAnalysisRequestId() + "/" + igsn + "/index.html";
                    File file = new File(indexFilePath);
                    indexWriter = new FileWriter(file);
    
                    String indexFileContents = String.format(indexTemplate, igsn, analysisRequest.getAnalysisRequestId()); 
                    indexWriter.write(indexFileContents);
                } 
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        if (indexWriter != null) {
                            indexWriter.close();
                        }
                    } 
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            // TODO: make this hard-coded value dynamic:
            String redboxAlertProcessor = "tima/dataset";
            
            // Create the alert folder if it doesn't exist
            File redboxAlertPathFile = new File(AnalysisRequestController.redboxPath + "/" + redboxAlertProcessor);
            if (!redboxAlertPathFile.exists()) {
                redboxAlertPathFile.mkdirs();
                LOGGER.warn("The alert path: " + redboxAlertPathFile + " did not exist and was created. It is likely that ReDBox is not monitoring this folder.");
            }
    
            Writer writer = null;
            try {
                File file = new File(redboxAlertPathFile.getAbsolutePath() + "/AR" + analysisRequest.getAnalysisRequestId() + ".csv");
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(sb.toString());
            } 
            catch (FileNotFoundException e) {
                e.printStackTrace();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (analysisRequest.getAnalysisCapability().getAnalysisCapabilityId() == 2) { // TODO: LEAP Specific
            // This is the point where we process the uploaded files:
            String script = AnalysisRequestController.scriptsPath + "/" + analysisRequest.getAnalysisCapability().getResultProcessorValue();
    
            StringBuilder sb = new StringBuilder("Title,Longitude,Latitude,GeospatialLocationDCMI,GeospatialLocationWKT,Description,RelatedWebsiteURL,RelatedWebsiteTitle,Keyword_2,LocationURL,ExtentOrQuality,DataSize,PublicationDate");
            for (Result result : analysisRequest.getResults()) {
                long projectFileSize = 0;

                StringBuffer output = new StringBuffer();
                Process p;
                try {
                    // These are examples of the arguments: python leap_import.py IECUR000Q.zip IECUR000Q 4
                    String command = "python " + script + " " + AnalysisRequestController.stagingPath + "/AR" + analysisRequestId + "GS" + result.getGeosample().getGeosampleId() + " " + result.getGeosample().getIgsn() + " " + analysisRequestId;
                    LOGGER.info("Running command: {}", command);
                    p = Runtime.getRuntime().exec(command);
                    p.waitFor();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    
                    String line = "";           
                    while ((line = reader.readLine()) != null) {
                        output.append(line);
                    }
                    
                    reader.close();
                    
                    String response = output.toString();
                    String[] fragments = response.split(";");
                    
                    result.setValue(fragments[0]);
                    projectFileSize = Long.parseLong(fragments[1]);
                    
                    this.resultResository.save(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                Geosample geosample = result.getGeosample();
                String igsn = geosample.getIgsn();
                double longitude = geosample.getLocation().getCoordinates()[0].x;
                double latitude = geosample.getLocation().getCoordinates()[0].y;
    
                // Create the CSV: TODO: all this needs to be refactored.
                
                // ADD THE ROW FOR THE APT DATA:
                sb.append(String.format("\nCAMECA LEAP 3D Atom Probe Analysis of IGSN:%s", igsn));
                sb.append("," + longitude);
                sb.append("," + latitude); 
                sb.append(",east=" + longitude + "; north=" + latitude + "; projection=WGS84");
                sb.append(",POINT(" + longitude + " " + latitude + ")");
                sb.append(String.format(",\"Atom probe tomography analysis of sample IGSN:%s. Generated with CAMECA LEAP 3D Atom Probe.\"", igsn));
    
                sb.append(String.format(",http://hdl.handle.net/10273/%s", igsn));
                sb.append(",International Geo Sample Number registry");
                sb.append(",Atom probe tomography");
                
                sb.append(String.format(",https://data.pawsey.org.au/public/?path=/Atom%20Probe%20Tomography/%d/%s", 
                        analysisRequest.getAnalysisRequestId(), 
                        igsn));
                
                sb.append(",\"1 RHIT File.\"");
                sb.append("," + humanReadableByteCount(projectFileSize, false));
                
                sb.append(new SimpleDateFormat(",yyyy-MM-dd").format(Calendar.getInstance().getTime()));
            }
            
            // TODO: make this hard-coded value dynamic:
            String redboxAlertProcessor = "leap/dataset";
            
            // Create the alert folder if it doesn't exist
            File redboxAlertPathFile = new File(AnalysisRequestController.redboxPath + "/" + redboxAlertProcessor);
            if (!redboxAlertPathFile.exists()) {
                redboxAlertPathFile.mkdirs();
                LOGGER.warn("The alert path: " + redboxAlertPathFile + " did not exist and was created. It is likely that ReDBox is not monitoring this folder.");
            }
    
            Writer writer = null;
            try {
                File file = new File(redboxAlertPathFile.getAbsolutePath() + "/AR" + analysisRequest.getAnalysisRequestId() + ".csv");
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(sb.toString());
            } 
            catch (FileNotFoundException e) {
                e.printStackTrace();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return "redirect:/AnalysisRequest/" + analysisRequest.getAnalysisRequestId();
    }   
}
