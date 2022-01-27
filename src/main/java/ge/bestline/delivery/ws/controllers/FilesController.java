package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dto.ResponseMessage;
import ge.bestline.delivery.ws.entities.Files;
import ge.bestline.delivery.ws.entities.Parcel;
import ge.bestline.delivery.ws.repositories.FilesRepository;
import ge.bestline.delivery.ws.repositories.ParcelRepository;
import ge.bestline.delivery.ws.services.FilesStorageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Log4j2
@RestController
@RequestMapping(path = "/files")
public class FilesController {

    private final FilesStorageService storageService;
    private final FilesRepository filesRepo;
    private final ParcelRepository parcelRepo;

    public FilesController(FilesStorageService storageService, FilesRepository filesRepo, ParcelRepository parcelRepo) {
        this.storageService = storageService;
        this.filesRepo = filesRepo;
        this.parcelRepo = parcelRepo;
    }

    @PostMapping("/upload")
    @Transactional
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file,
                                                      @RequestParam(value = "parcelId", required = false) Integer parcelId) {
        log.info("uploading file");
        String message;
        try {
            Parcel parcel;
            String uploadedFileName = storageService.save(file, parcelId);
            if (parcelId != null) {
                parcel = parcelRepo.findById(parcelId).orElseThrow(() -> new ResourceNotFoundException("Can't find Parcel Using This ID=" + parcelId));
                filesRepo.save(new Files(uploadedFileName, parcel));
            } else {
                log.warn("uploading file without ParcelID");
            }
            message = "Uploaded successfully: " + uploadedFileName;
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (ResourceNotFoundException e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "! " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
