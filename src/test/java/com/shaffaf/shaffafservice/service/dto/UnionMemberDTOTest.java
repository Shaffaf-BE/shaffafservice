package com.shaffaf.shaffafservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UnionMemberDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UnionMemberDTO.class);
        UnionMemberDTO unionMemberDTO1 = new UnionMemberDTO();
        unionMemberDTO1.setId(1L);
        UnionMemberDTO unionMemberDTO2 = new UnionMemberDTO();
        assertThat(unionMemberDTO1).isNotEqualTo(unionMemberDTO2);
        unionMemberDTO2.setId(unionMemberDTO1.getId());
        assertThat(unionMemberDTO1).isEqualTo(unionMemberDTO2);
        unionMemberDTO2.setId(2L);
        assertThat(unionMemberDTO1).isNotEqualTo(unionMemberDTO2);
        unionMemberDTO1.setId(null);
        assertThat(unionMemberDTO1).isNotEqualTo(unionMemberDTO2);
    }
}
