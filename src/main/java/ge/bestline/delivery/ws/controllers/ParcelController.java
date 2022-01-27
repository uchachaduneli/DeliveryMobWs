package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.ParcelDao;
import ge.bestline.delivery.ws.entities.Packages;
import ge.bestline.delivery.ws.entities.Parcel;
import ge.bestline.delivery.ws.entities.ParcelStatusHistory;
import ge.bestline.delivery.ws.repositories.PackagesRepository;
import ge.bestline.delivery.ws.repositories.ParcelRepository;
import ge.bestline.delivery.ws.repositories.ParcelStatusReasonRepository;
import ge.bestline.delivery.ws.repositories.ParselStatusHistoryRepo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/parcel")
public class ParcelController {

    private final ParcelRepository repo;
    private final PackagesRepository packagesRepo;
    private final ParcelDao dao;
    private final ParselStatusHistoryRepo statusHistoryRepo;
    private final ParcelStatusReasonRepository statusReasonRepo;

    public ParcelController(ParcelRepository repo,
                            PackagesRepository packagesRepo,
                            ParcelDao dao, ParselStatusHistoryRepo statusHistoryRepo,
                            ParcelStatusReasonRepository statusReasonRepo) {
        this.repo = repo;
        this.packagesRepo = packagesRepo;
        this.dao = dao;
        this.statusHistoryRepo = statusHistoryRepo;
        this.statusReasonRepo = statusReasonRepo;
    }

    @GetMapping(path = "/statusHistory/{id}")
    public ResponseEntity<List<ParcelStatusHistory>> getParcelStatusHistoryByParcelId(@PathVariable Integer id) {
        log.info("Getting ParcelStatusHistory By Parcel ID: " + id);
        return ResponseEntity.ok(statusHistoryRepo.findByParcelId(id));
    }

    @PutMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<Parcel> updateById(@PathVariable Integer id, @RequestBody Parcel request) {
        log.info("Updating Parcel");
        Parcel existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
        if (request.getStatus().getId() != existing.getStatus().getId()) {
            statusHistoryRepo.save(new ParcelStatusHistory(existing
                    , existing.getStatus().getStatus().getName()
                    , existing.getStatus().getStatus().getCode()
                    , existing.getStatus().getName()
            ));
            existing.setStatus(request.getStatus());
        }
        Parcel updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @GetMapping(path = "/userParcels/{userId}")
    @Operation(summary = "Returns parcels for user, You can fIlter with status id 1-NEW / 2-SEEN / 3-TAKEN",
            operationId = "getByUserId",
            description = "Returns parcels for user, You can fIlter with status id 1-NEW / 2-SEEN / 3-TAKEN")
    public ResponseEntity<Map<String, Object>> getByUserId(@RequestParam Integer status) {//1 axali, 2 nanaxi, 3 agebuli
        return new ResponseEntity<>(dao.findAll(0, 10, new Parcel()), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public Parcel getById(@PathVariable Integer id) {
        log.info("Getting Parcel With ID: " + id);
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

    @GetMapping(path = "/package/{id}")
    public ResponseEntity<List<Packages>> getPackagesByParcelId(@PathVariable Integer id) {
        log.info("Getting Packages By Parcel ID: " + id);
        return ResponseEntity.ok(packagesRepo.findByParcelId(id));
    }
}
