/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package org.semanticweb.owlapi.change;

import static org.semanticweb.owlapi.search.EntitySearcher.isDefined;
import static org.semanticweb.owlapi.search.Searcher.sub;
import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * For a given class, this composite change makes its told primitive subclasses
 * mutually disjoint. For example, if B, C, and D are primitive subclasses of A
 * then this composite change will make B, C, and D mutually disjoint. <br>
 * More formally, for a given class, A, and a set of ontologies, S, this method
 * will obtain a set of classes, G, where all classes in G are named and
 * primitive. Moreover, for any class, B in G, some ontology O in S will contain
 * an axiom, SubClassOf(B, A). All classes in G will be made mutually disjoint
 * by creating axiom(s) in a target ontology T. <br>
 * This composite change supports a common design pattern where primitive
 * subclasses of a class are made mutually disjoint.
 * 
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.1.0
 */
public class MakePrimitiveSubClassesMutuallyDisjoint extends
        AbstractCompositeOntologyChange {

    private static final long serialVersionUID = 40000L;

    /**
     * Instantiates a new make primitive sub classes mutually disjoint.
     * 
     * @param dataFactory
     *        the datafactory to use
     * @param cls
     *        the class to convert
     * @param targetOntology
     *        the target ontology
     */
    public MakePrimitiveSubClassesMutuallyDisjoint(
            @Nonnull OWLDataFactory dataFactory, @Nonnull OWLClass cls,
            @Nonnull OWLOntology targetOntology) {
        this(dataFactory, cls, targetOntology, false);
    }

    /**
     * Instantiates a new make primitive sub classes mutually disjoint.
     * 
     * @param dataFactory
     *        the datafactory to use
     * @param cls
     *        the class to convert
     * @param targetOntology
     *        the target ontology
     * @param usePairwiseDisjointAxioms
     *        true if pairwise disjoint axioms should be used
     */
    public MakePrimitiveSubClassesMutuallyDisjoint(
            @Nonnull OWLDataFactory dataFactory, @Nonnull OWLClass cls,
            @Nonnull OWLOntology targetOntology,
            boolean usePairwiseDisjointAxioms) {
        super(dataFactory);
        generateChanges(checkNotNull(cls, "cls cannot be null"),
                checkNotNull(targetOntology, "targetOntology cannot be null"),
                usePairwiseDisjointAxioms);
    }

    private void generateChanges(@Nonnull OWLClass cls,
            @Nonnull OWLOntology targetOntology,
            boolean usePairwiseDisjointAxioms) {
        Set<OWLClass> subclasses = new HashSet<>();
        Collection<OWLClassExpression> sub = sub(
                targetOntology.getSubClassAxiomsForSuperClass(cls),
                OWLClassExpression.class);
        for (OWLClassExpression subCls : sub) {
            if (!subCls.isAnonymous()
                    && !isDefined(subCls.asOWLClass(), targetOntology)) {
                subclasses.add(subCls.asOWLClass());
            }
        }
        MakeClassesMutuallyDisjoint makeClassesMutuallyDisjoint = new MakeClassesMutuallyDisjoint(
                getDataFactory(), subclasses, usePairwiseDisjointAxioms,
                targetOntology);
        addChanges(makeClassesMutuallyDisjoint.getChanges());
    }
}
