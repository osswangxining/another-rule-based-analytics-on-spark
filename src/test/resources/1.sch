<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron"
                 icon="http://www.ascc.net/xml/resource/schematron/bilby.jpg"
                 defaultPhase="built">

             <sch:p>This is an example schema for the <emph>Building Projects XML</emph> language.</sch:p>

             <sch:phase id="underConstruction">
                <sch:active pattern="construction"></sch:active>
                <sch:active pattern="admin"></sch:active>
             </sch:phase>

             <sch:phase id="built">
                <sch:active pattern="completed">completed</sch:active>
                <sch:active pattern="admin">admin</sch:active>
             </sch:phase>


             <sch:pattern name="Construction Checks" id="completed">

                <sch:p>Constraints which are applied during construction</sch:p>

                <sch:rule context="/house">
                   <sch:assert test="count(wall) &lt; 4 ">A house should have 1-4 walls</sch:assert>
                   <sch:report test="not(roof)">The house is incomplete, it still needs a roof</sch:report>
                   <sch:assert test="builder">An incomplete house must have a builder assigned to it</sch:assert>
                   <sch:assert test="not(owner)">An incomplete house cannot have an owner</sch:assert>
                </sch:rule>

             </sch:pattern>

             <sch:pattern name="Final Checks" id="admin2">
                <sch:p>Constraints which are applied after construction</sch:p>

                <sch:rule context="/house">
                   <sch:assert test="count(wall) = 4">A house should have 4 walls</sch:assert>
                   <sch:report test="roof">The house is incomplete, it still needs a roof</sch:report>
                   <sch:assert test="owner">An incomplete house must have an owner</sch:assert>
                   <sch:assert test="not(builder)">An incomplete house doesn't need a builder</sch:assert>
                </sch:rule>

             </sch:pattern>

         </sch:schema>