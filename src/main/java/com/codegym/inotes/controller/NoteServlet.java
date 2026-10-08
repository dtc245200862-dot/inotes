package com.codegym.inotes.controller;

import com.codegym.inotes.model.Note;
import com.codegym.inotes.model.NoteType;
import com.codegym.inotes.strategy.NoteDBStrategy;
import com.codegym.inotes.strategy.NoteFileStrategy;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller Servlet xử lý điều hướng các yêu cầu người dùng cho ứng dụng iNotes.
 */
@WebServlet(name = "NoteServlet", urlPatterns = {"/notes", ""})
public class NoteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private NoteManagement noteManagement;

    @Override
    public void init() throws ServletException {
        super.init();
        noteManagement = NoteManagement.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        syncStrategyFromSession(request);

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "create":
                showCreateForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "view":
                showViewForm(request, response);
                break;
            case "delete":
                deleteNote(request, response);
                break;
            case "switchStorage":
                switchStorage(request, response);
                break;
            case "search":
                searchNotes(request, response);
                break;
            case "list":
            default:
                listNotes(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        syncStrategyFromSession(request);

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "insert":
                insertNote(request, response);
                break;
            case "update":
                updateNote(request, response);
                break;
            case "switchStorage":
                switchStorage(request, response);
                break;
            default:
                listNotes(request, response);
                break;
        }
    }

    /**
     * Đồng bộ hóa chiến lược lưu trữ (Strategy) từ Session của người dùng.
     */
    private void syncStrategyFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String currentStorage = (String) session.getAttribute("storageType");
        if (currentStorage == null) {
            currentStorage = "DB";
            session.setAttribute("storageType", "DB");
        }

        if ("FILE".equalsIgnoreCase(currentStorage)) {
            if (!"FILE".equals(noteManagement.getStrategy().getStorageTypeKey())) {
                noteManagement.setStrategy(new NoteFileStrategy());
            }
        } else {
            if (!"DB".equals(noteManagement.getStrategy().getStorageTypeKey())) {
                noteManagement.setStrategy(new NoteDBStrategy());
            }
        }
    }

    private void listNotes(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Note> listNotes = noteManagement.getAllNotes();
        List<NoteType> listTypes = noteManagement.getAllNoteTypes();

        request.setAttribute("listNotes", listNotes);
        request.setAttribute("listTypes", listTypes);
        request.setAttribute("currentStrategyName", noteManagement.getStrategy().getStrategyName());
        request.setAttribute("currentStorageKey", noteManagement.getStrategy().getStorageTypeKey());

        RequestDispatcher dispatcher = request.getRequestDispatcher("/notes/list.jsp");
        dispatcher.forward(request, response);
    }

    private void searchNotes(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<Note> listNotes = noteManagement.searchNotes(keyword);
        List<NoteType> listTypes = noteManagement.getAllNoteTypes();

        request.setAttribute("listNotes", listNotes);
        request.setAttribute("listTypes", listTypes);
        request.setAttribute("keyword", keyword);
        request.setAttribute("currentStrategyName", noteManagement.getStrategy().getStrategyName());
        request.setAttribute("currentStorageKey", noteManagement.getStrategy().getStorageTypeKey());

        RequestDispatcher dispatcher = request.getRequestDispatcher("/notes/list.jsp");
        dispatcher.forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<NoteType> listTypes = noteManagement.getAllNoteTypes();
        request.setAttribute("listTypes", listTypes);
        request.setAttribute("currentStrategyName", noteManagement.getStrategy().getStrategyName());
        request.setAttribute("currentStorageKey", noteManagement.getStrategy().getStorageTypeKey());

        RequestDispatcher dispatcher = request.getRequestDispatcher("/notes/create.jsp");
        dispatcher.forward(request, response);
    }

    private void insertNote(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        int typeId = 0;
        try {
            typeId = Integer.parseInt(request.getParameter("typeId"));
        } catch (NumberFormatException ignored) {}

        noteManagement.addNote(title, content, typeId);
        response.sendRedirect(request.getContextPath() + "/notes?message=created");
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Note note = noteManagement.getNoteById(id);
        List<NoteType> listTypes = noteManagement.getAllNoteTypes();

        request.setAttribute("note", note);
        request.setAttribute("listTypes", listTypes);
        request.setAttribute("currentStrategyName", noteManagement.getStrategy().getStrategyName());
        request.setAttribute("currentStorageKey", noteManagement.getStrategy().getStorageTypeKey());

        RequestDispatcher dispatcher = request.getRequestDispatcher("/notes/edit.jsp");
        dispatcher.forward(request, response);
    }

    private void updateNote(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        int typeId = 0;
        try {
            typeId = Integer.parseInt(request.getParameter("typeId"));
        } catch (NumberFormatException ignored) {}

        Note note = noteManagement.getNoteById(id);
        if (note != null) {
            note.setTitle(title);
            note.setContent(content);
            note.setTypeId(typeId);
            noteManagement.saveNote(note);
        }

        response.sendRedirect(request.getContextPath() + "/notes?message=updated");
    }

    private void showViewForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Note note = noteManagement.getNoteById(id);
        request.setAttribute("note", note);
        request.setAttribute("currentStrategyName", noteManagement.getStrategy().getStrategyName());
        request.setAttribute("currentStorageKey", noteManagement.getStrategy().getStorageTypeKey());

        RequestDispatcher dispatcher = request.getRequestDispatcher("/notes/view.jsp");
        dispatcher.forward(request, response);
    }

    private void deleteNote(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        noteManagement.deleteNote(id);
        response.sendRedirect(request.getContextPath() + "/notes?message=deleted");
    }

    private void switchStorage(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String targetStorage = request.getParameter("targetStorage");
        if ("FILE".equalsIgnoreCase(targetStorage)) {
            request.getSession().setAttribute("storageType", "FILE");
            noteManagement.setStrategy(new NoteFileStrategy());
        } else {
            request.getSession().setAttribute("storageType", "DB");
            noteManagement.setStrategy(new NoteDBStrategy());
        }
        response.sendRedirect(request.getContextPath() + "/notes?message=storage_switched");
    }
}
