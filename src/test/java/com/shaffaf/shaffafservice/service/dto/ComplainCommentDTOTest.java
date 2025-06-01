package com.shaffaf.shaffafservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ComplainCommentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ComplainCommentDTO.class);
        ComplainCommentDTO complainCommentDTO1 = new ComplainCommentDTO();
        complainCommentDTO1.setId(1L);
        ComplainCommentDTO complainCommentDTO2 = new ComplainCommentDTO();
        assertThat(complainCommentDTO1).isNotEqualTo(complainCommentDTO2);
        complainCommentDTO2.setId(complainCommentDTO1.getId());
        assertThat(complainCommentDTO1).isEqualTo(complainCommentDTO2);
        complainCommentDTO2.setId(2L);
        assertThat(complainCommentDTO1).isNotEqualTo(complainCommentDTO2);
        complainCommentDTO1.setId(null);
        assertThat(complainCommentDTO1).isNotEqualTo(complainCommentDTO2);
    }
}
