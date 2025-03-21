package com.ignit.internship.service.belajaryuk;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ignit.internship.dto.belajaryuk.StudyPackageRequest;
import com.ignit.internship.dto.payment.PaymentNotificationRequest;
import com.ignit.internship.dto.payment.TransactionResponse;
import com.ignit.internship.enums.PaymentStatus;
import com.ignit.internship.exception.AlreadyBoughtException;
import com.ignit.internship.exception.IdNotFoundException;
import com.ignit.internship.model.belajaryuk.StudyPackage;
import com.ignit.internship.model.profile.UserProfile;
import com.ignit.internship.model.utils.Image;
import com.ignit.internship.repository.belajaryuk.StudyPackageRepository;
import com.ignit.internship.repository.profile.ProfileRepository;
import com.ignit.internship.repository.utils.TagRepository;
import com.ignit.internship.service.payment.PaymentService;
import com.ignit.internship.service.utils.ImageService;

import jakarta.transaction.Transactional;

@Service
public class StudyPackageService {

    private final StudyPackageRepository studyPackageRepository;

    private final TagRepository tagRepository;

    private final PaymentService paymentService;

    private final ImageService imageService;

    private final ProfileRepository profileRepository;

    public StudyPackageService(
        final StudyPackageRepository studyPackageRepository, 
        final TagRepository tagRepository,
        final PaymentService paymentService,
        final ImageService imageService,
        final ProfileRepository profileRepository
    ) {
        this.studyPackageRepository = studyPackageRepository;
        this.tagRepository = tagRepository;
        this.paymentService = paymentService;
        this.imageService = imageService;
        this.profileRepository = profileRepository;
    }


    public StudyPackage getStudyPackageById(Long id) throws IdNotFoundException {
        return studyPackageRepository.findById(id).orElseThrow(() -> new IdNotFoundException("Study Package not found"));
    }

    public List<StudyPackage> getStudyPackageByPage(Pageable pageable) {
        return studyPackageRepository.findAll(pageable).toList();
    }

    public List<StudyPackage> getStudyPackageByPageAndTag(Pageable pageable, String tag) {
        return studyPackageRepository.findByTagName(tag, pageable).toList();
    }

    public List<StudyPackage> getOwnedStudyPackageByPageAndTag(Pageable pageable, Long id) {
        return studyPackageRepository.findAllByProfileId(id, pageable).toList();
    }

    @Transactional
    public StudyPackage createStudyPackage(MultipartFile file, StudyPackageRequest request) throws Exception {
        Image image = imageService.uploadImage(file);

        StudyPackage studyPackage = studyPackageRepository.save(new StudyPackage(
            request.getTitle(),
            request.getSubtitle(),
            image.getId(),
            request.getPrice(),
            tagRepository.findById(request.getTag()).orElseThrow(() -> new IdNotFoundException("Tag not found"))
        ));
        
        return studyPackage;
    }

    @Transactional
    public StudyPackage updateStudyPackage(Long id, StudyPackageRequest request) throws IdNotFoundException {
        if (!tagRepository.existsById(request.getTag())) {
            throw new IdNotFoundException("Tag not found");
        }

        StudyPackage studyPackage = getStudyPackageById(id);
        
        studyPackage.setTitle(request.getTitle());
        studyPackage.setSubtitle(request.getSubtitle());
        studyPackage.setPrice(request.getPrice());
        studyPackage.setTag(tagRepository.findById(request.getTag()).orElseThrow(() -> new IdNotFoundException("Tag not found")));

        return studyPackageRepository.save(studyPackage);
    }

    public void deleteStudyPackage(Long id) throws IdNotFoundException {
        StudyPackage studyPackage = getStudyPackageById(id);
        imageService.deleteImage(studyPackage.getImageId());
        studyPackageRepository.deleteById(id);
    }

    public TransactionResponse createStudyPackageTransaction(Long profileId, Long packageId) throws Exception {
        if (studyPackageRepository.existsByProfileIdAndPackageId(profileId, packageId)) {
            throw new AlreadyBoughtException("Profile already bought the study package");
        }
        
        StudyPackage studyPackage = getStudyPackageById(packageId);
        return paymentService.createTransaction(profileId, studyPackage.getId(), studyPackage.getPrice());
    }

    public void processStudyPackagePayment(PaymentNotificationRequest request) throws IllegalArgumentException, IdNotFoundException {
        if (paymentService.verifyPayment(request) != PaymentStatus.SUCCESS) {
            return;
        }
        
        String[] orderId = request.getOrderId().split("-");
        Long packageId = Long.parseLong(orderId[0]);
        Long profileId = Long.parseLong(orderId[1]);

        if (studyPackageRepository.existsByProfileIdAndPackageId(profileId, packageId)) {
            return;
        }

        StudyPackage studyPackage = getStudyPackageById(packageId);

        UserProfile profile = profileRepository.findById(profileId).orElseThrow(() -> new IdNotFoundException("Profile not found"));

        studyPackage.addProfile(profile);
        profile.addStudyPackage(studyPackage);

        studyPackageRepository.save(studyPackage);

        System.out.println("success!");
    }
}
