package com.microservices.quizservice.service;

import com.microservices.quizservice.feign.QuizInterface;
import com.microservices.quizservice.model.QuestionWrapper;
import com.microservices.quizservice.model.Quiz;
import com.microservices.quizservice.model.Response;
import com.microservices.quizservice.repository.QuizRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    private QuizInterface quizInterface;

    @Autowired
    public QuizService(QuizInterface quizInterface) {
        this.quizInterface = quizInterface;
    }

    public ResponseEntity<String> createQuiz(String category, Integer numQ, String title) {
        List<Integer> questions = quizInterface.getQuestionsForQuiz(category, numQ).getBody();
        log.info("--------------------------------");
        log.info("Questions: {}", questions);
        log.info("--------------------------------");
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionIds(questions);
        quizRepository.save(quiz);

        return new ResponseEntity<>("Quiz created successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        Quiz quiz = quizRepository.findById(Long.valueOf(id)).get();
        List<Integer> questionIds = quiz.getQuestionIds();
        ResponseEntity<List<QuestionWrapper>> questions = quizInterface.getQuestionsFromId(questionIds);
        return questions;
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        ResponseEntity<Integer> score = quizInterface.getScore(responses);
        return score;
    }
}
