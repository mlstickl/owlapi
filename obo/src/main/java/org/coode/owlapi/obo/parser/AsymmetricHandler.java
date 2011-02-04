package org.coode.owlapi.obo.parser;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 06-Mar-2007<br><br>
 */
public class AsymmetricHandler extends AbstractTagValueHandler {


    public AsymmetricHandler(OBOConsumer consumer) {
        super(OBOVocabulary.IS_ASYMMETRIC.getName(), consumer);
    }


    public void handle(String id, String value, String comment) {
        if (Boolean.parseBoolean(value)) {
            OWLObjectProperty prop = getOWLObjectProperty(id);
            OWLAxiom ax = getDataFactory().getOWLAsymmetricObjectPropertyAxiom(prop);
            applyChange(new AddAxiom(getOntology(), ax));
        }
        else {
            addAnnotation(id, OBOVocabulary.IS_ASYMMETRIC.getName(), getBooleanConstant(false));
        }
    }
}
