package com.HRMS.HRMS.service.Travel;

import com.HRMS.HRMS.dto.CustomUserPrincipal;
import com.HRMS.HRMS.dto.TravelDtos.TravelDocCreateDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelDocResponseDto;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.Enums.DocType;
import com.HRMS.HRMS.entity.TravelEntities.Travel;
import com.HRMS.HRMS.entity.TravelEntities.TravelDoc;
import com.HRMS.HRMS.exception.BadRequestException;
import com.HRMS.HRMS.exception.ForBiddenException;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.repository.TravelRepositories.TravelAssignmentRepo;
import com.HRMS.HRMS.repository.TravelRepositories.TravelDocRepository;
import com.HRMS.HRMS.repository.TravelRepositories.TravelRepository;
import com.HRMS.HRMS.service.DocumentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TravelDocService {

    private final TravelDocRepository travelDocRepository;
    private final TravelRepository travelRepository;
    private final EmployeeRepository employeeRepository;
    private final DocumentService documentService; // Your Cloudinary Service
    private final ModelMapper modelMapper;
    private final TravelAssignmentRepo travelAssignmentRepo;

    @Autowired
    public TravelDocService(
            TravelDocRepository travelDocRepository,
            TravelRepository travelRepository,
            EmployeeRepository employeeRepository,
            DocumentService documentService,
            ModelMapper modelMapper,
            TravelAssignmentRepo travelAssignmentRepo){
        this.travelDocRepository = travelDocRepository;
        this.documentService = documentService;
        this.modelMapper = modelMapper;
        this.employeeRepository = employeeRepository;
        this.travelRepository = travelRepository;
        this.travelAssignmentRepo = travelAssignmentRepo;
    }

    public TravelDocResponseDto uploadDocument(TravelDocCreateDto travelDocCreateDto , CustomUserPrincipal user) {

        Travel travel = travelRepository.findById(travelDocCreateDto.getTravelId())
                .orElseThrow(() -> new ResourceNotFoundException("Travel not found"));

        //now hr and employee assign to that travel only can upload document
        Employee uploader = employeeRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Uploader not found!!!!!!"));
        if( user.getRole().equals("EMPLOYEE") && !travelAssignmentRepo.existsByTravelIdAndEmployeeId(travel.getId(),uploader.getId())){
            throw new ForBiddenException("You can not upload document for this travel !!");
        }
        if(user.getRole().equals("HR") && !travel.getCreatedBy().getId().equals(user.getId())){
            throw new ForBiddenException("You can not upload document for this travel !! !!");
        }

        TravelDoc doc = new TravelDoc();
        doc.setTravel(travel);
        doc.setUploadedBy(uploader);
        doc.setDocType(DocType.valueOf(travelDocCreateDto.getDocTypeStr()));

        if (travelDocCreateDto.getOwnerId() != null) {
            Employee owner = employeeRepository.findById(travelDocCreateDto.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
            doc.setOwner(owner);
        } else {
            doc.setOwner(null); // this document is for all
        }

        String fileUrl = documentService.uploadFile(travelDocCreateDto.getFile(), "travel", user.getId(), false);
        doc.setFileUrl(fileUrl);

        TravelDoc savedDoc = travelDocRepository.save(doc);
        return mapToResponse(savedDoc);
    }

    public List<TravelDocResponseDto> getAllDocsForTravel(Long travelId,CustomUserPrincipal user) {
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException("Travel not found"));

        if ("HR".equals(user.getRole()) &&
                !user.getId().equals(travel.getCreatedBy().getId())) {
            throw new ForBiddenException("you can't access this documents!!");
        }
        return travelDocRepository.findByTravelId(travelId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<TravelDocResponseDto> getDocsForEmployee(Long travelId, Long employeeId) {
        return travelDocRepository.findByTravelIdAndEmployeeId(travelId, employeeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<TravelDocResponseDto> getGeneralDocs(Long travelId) {
        return travelDocRepository.findByTravelIdAndOwnerIsNull(travelId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TravelDocResponseDto updateDocumentFile(Long docId, MultipartFile newFile,CustomUserPrincipal user) {
        TravelDoc doc = travelDocRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        if(doc.getUploadedBy().getId().equals(user.getId())){
         throw new BadRequestException("you can not update this document !!!");
        }
        String newUrl = documentService.uploadFile(newFile, "travel", doc.getUploadedBy().getId(), false);
        doc.setFileUrl(newUrl);
        return mapToResponse(travelDocRepository.save(doc));
    }

    public void deleteDocument(Long docId) {
        if (!travelDocRepository.existsById(docId)) {
            throw new ResourceNotFoundException("Document not found");
        }
        travelDocRepository.deleteById(docId);
    }


    private TravelDocResponseDto mapToResponse(TravelDoc travelDoc) {
        TravelDocResponseDto dto = modelMapper.map(travelDoc, TravelDocResponseDto.class);
        dto.setTravelId(travelDoc.getTravel().getId());
        dto.setUploadedById(travelDoc.getUploadedBy().getId());
        dto.setUploadedByName(travelDoc.getUploadedBy().getName());
        if (travelDoc.getOwner() != null) {
            dto.setOwnerId(travelDoc.getOwner().getId());
            dto.setOwnerName(travelDoc.getOwner().getName());
        }
        dto.setUploadedAt(travelDoc.getCreatedAt());
        return dto;
    }
}
