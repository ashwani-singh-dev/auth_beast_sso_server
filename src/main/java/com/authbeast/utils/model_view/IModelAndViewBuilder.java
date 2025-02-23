package com.zentois.authbeast.utils.model_view;

import java.util.List;

import org.springframework.web.servlet.ModelAndView;

import com.zentois.authbeast.dto.ModelAndViewAttribute;

/**
 * Interface for building ModelAndView objects for specified views and attributes.
 * Implementations of this interface should define the logic for constructing
 * a ModelAndView instance based on the provided view name and model attributes.
 * 
 * This interface is particularly useful in MVC architectures where views need
 * to be rendered dynamically with varying sets of attributes.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Sep-17
 */
@FunctionalInterface
public interface IModelAndViewBuilder
{
    /**
     * Builds a ModelAndView object for the specified view name and attributes.
     * 
     * @param viewName The name of the view to render.
     * @param modelAttributes The list of attributes to add to the model.
     * @return A ModelAndView object for the specified view name and attributes.
     */
    ModelAndView build(String viewName, List<ModelAndViewAttribute> modelAttributes);
}