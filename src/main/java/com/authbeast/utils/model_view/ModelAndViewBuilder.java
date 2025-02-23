package com.zentois.authbeast.utils.model_view;

import java.util.List;

import org.springframework.web.servlet.ModelAndView;

import com.zentois.authbeast.dto.ModelAndViewAttribute;

/**
 * Builds a ModelAndView object for the given view name and attributes.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Dec-31
 */
public class ModelAndViewBuilder implements IModelAndViewBuilder
{
    /**
     * Builds a ModelAndView object for the given view name and attributes.
     * 
     * @param viewName The name of the view to render.
     * @param modelAttributes The list of attributes to add to the model.
     * @return A ModelAndView object for the given view name and attributes.
     */
    @Override
    public ModelAndView build(String viewName, List<ModelAndViewAttribute> modelAttributes)
    {
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(viewName);

        if (modelAttributes != null)
        {
            modelAttributes.forEach(attr -> modelAndView.addObject(attr.getName(), attr.getValue()));
        }
        return modelAndView;
    }
}
