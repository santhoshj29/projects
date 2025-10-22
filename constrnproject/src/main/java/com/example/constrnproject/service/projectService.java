package com.example.constrnproject.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.constrnproject.model.Task;
import com.example.constrnproject.model.project;
import com.example.constrnproject.repository.Taskrepo;
import com.example.constrnproject.repository.projectrepo;

@Service
public class projectService {

    private final projectrepo projectRepo;
    private final Taskrepo taskRepo;

    @Autowired
    public projectService(projectrepo projectRepo, Taskrepo taskRepo) {
        this.projectRepo = projectRepo;
        this.taskRepo = taskRepo;
    }

    public project registerProject(project Project){
        projectRepo.save(Project);
        return Project;
    }
    public project findByProjectId(Long projectId){

        return projectRepo.findById(projectId).orElse(null);

    }

    public double calculateProjectCompletion(long projectId){
        List<Task> tasks = taskRepo.findByProject_ProjectId(projectId);
        if (tasks.isEmpty()) {
            return 0.0; // No tasks means 0% completion
        }

        double avgOfCompletion = tasks.stream()
                .mapToDouble(Task::getCompletionPercentage)
                .average()
                .orElse(0.0);
                
        project Project = projectRepo.findById(projectId).orElse(null);
        if(Project != null){
            Project.setcompletionPercentage(avgOfCompletion);
            projectRepo.save(Project);
        }

        return avgOfCompletion;

    }

    public boolean isProjectDelayed(long projectId){

        project Project = projectRepo.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        

        boolean delayed = Project.getendDate() != null && Project.getendDate().isBefore(java.time.LocalDate.now()) && Project.getcompletionPercentage() < 100.0;

        if(delayed){
            Project.setstatus("Delayed");
            projectRepo.save(Project);
        }

        return delayed;
    }

    public BigDecimal calculateCostOverrun(long projectId) {
        project proj = projectRepo.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));

        if (proj.getbudget() == null || proj.getactualamount() == null)
            return BigDecimal.ZERO;

        BigDecimal overrun = proj.getactualamount().subtract(proj.getbudget());
        return overrun;
    }

    public String getProjectSummary(long projectId) {
        project proj = projectRepo.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));

        return String.format(
            "Project: %s | Status: %s | Completion: %.2f%% | Budget: %s | Overrun: %s",
            proj.getprojectName(),
            proj.getstatus(),
            proj.getcompletionPercentage(),
            proj.getbudget(),
            calculateCostOverrun(projectId)
        );
    }


}
