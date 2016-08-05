package com.project.db.domain.userMaint

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Created with IntelliJ IDEA.
 * User: kevin.torbett
 * Date: 7/20/15
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="IM_APPLICATIONS")
class UserApplicationView implements Serializable, Cloneable {
        @Id
        @Column(name = "ID", nullable = false, length = 22, precision = 0)
        Long id;

        @Column(name = "TAGGED_YN", nullable = false, length = 22, precision = 0)
        Long taggedYN;


        @Column(name = "APPLICATION_NAME", length = 40, precision = 0)
        String applicationName = '';




    }